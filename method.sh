#!/bin/bash

curl -X POST http://localhost:9000/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"local","descripcion":"John Doe","precio":20.3,"stock":110, "stock_minimo":30, "id_categoria": 1}'
