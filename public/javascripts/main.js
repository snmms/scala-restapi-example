document.getElementById("fch").addEventListener("click", () => {
  fetch("http://localhost:9000/api/v1/productos")
    .then((res) => res.json())
    .then((productos) => {
      const data = document.getElementById("data");

      data.innerHTML = productos
        .map(
          (x) => `
	  <thead>
	    <tr>
	      <th scope="col">Nombre</th>
	      <th scope="col">Descripción</th>
	      <th scope="col">Precio</th>
	      <th scope="col">Stock</th>
	      <th scope="col">Categoria</th>
	    </tr>
	  </thead>
	  <tbody>
	    <tr>
	      <td>${x.nombre}</td>
	      <td>${x.descripcion}</td>
	      <td>${x.precio}</td>
	      <td>${x.stock}</td>
	      <td>${x.id_categoria}</td>
	    </tr>
	  </tbody>`,
        )
        .join("");
    })
    .catch((error) => {
      console.error("Error:", error);
    });
});

document.getElementById("send").addEventListener("click", async () => {
  const dataEl = document.getElementById("data");

  const nombre = document.getElementById("nombre").value.trim();
  const precio = document.getElementById("precio").value;
  const stock = document.getElementById("stock").value;
  const id_categoria = document.getElementById("id_categoria").value;

  if (!nombre || !precio || !stock || !id_categoria) {
    dataEl.style.color = "red";
    dataEl.textContent =
      "Error: nombre, precio, stock e id_categoria son obligatorios";
    return;
  }

  const precioNum = Number(precio);
  const stockNum = Number(stock);

  if (isNaN(precioNum) || precioNum < 0) {
    dataEl.style.color = "red";
    dataEl.textContent = "Error: precio debe ser un número >= 0";
    return;
  }
  if (isNaN(stockNum) || stockNum < 0) {
    dataEl.style.color = "red";
    dataEl.textContent = "Error: stock debe ser un número >= 0";
    return;
  }

  const producto = {
    nombre,
    descripcion: document.getElementById("descripcion").value.trim() || null,
    precio: precioNum,
    stock: stockNum,
    stock_minimo: Number(document.getElementById("stock_minimo").value) || 5,
    id_categoria: Number(id_categoria),
  };

  try {
    console.log(JSON.stringify(producto));
    const response = await fetch("http://localhost:9000/api/v1/productos", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(producto),
    });

    const data = await response.json();

    if (!response.ok) {
      dataEl.style.color = "red";
      dataEl.textContent = `Error ${response.status}: ${data.error || JSON.stringify(data)}`;
      return;
    }

    dataEl.style.color = "green";
    dataEl.textContent = `Producto creado: ${data.nombre} (ID: ${data.id_producto})`;

    document.getElementById("nombre").value = "";
    document.getElementById("descripcion").value = "";
    document.getElementById("precio").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("stock_minimo").value = "";
    document.getElementById("id_categoria").value = "";

    document.getElementById("fch").click();
  } catch (error) {
    dataEl.style.color = "red";
    dataEl.textContent = `Error de red: ${error.message}`;
  }
});
