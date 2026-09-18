#!/bin/bash

curl -X POST http://localhost:9000/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"local","descripcion":"John Doe","precio":20.3,"stock":110, "stock_minimo":30, "id_categoria": 1}'

curl -X POST http://localhost:9000/api/v1/ventas -H "Content-Type: application/json" -d '{"items":[{"id_producto":1,"cantidad":2},{"id_producto":2,"cantidad":1}]}'
