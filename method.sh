#!/bin/bash

curl -X GET http://localhost:9000/api/v1/productos/2

# create
curl -X POST http://localhost:9000/api/v1/productos -H "Content-Type: application/json" -d '{"nombre":"Parlante Flip","descripcion":"Parlante Bluetooth","precio":300.9,"stock":100,"stock_minimo":10,"id_categoria":6}'

# edit
curl -X PUT http://localhost:9000/api/v1/productos/14 -H "Content-Type: application/json" -d '{"id_producto":14,"nombre":"Parlante JBL Flip 6","descripcion":"Parlante Bluetooth portátil","precio":499.9,"stock":1000,"stock_minimo":100,"id_categoria":6}'

# venta
curl -X POST http://localhost:9000/api/v1/ventas -H "Content-Type: application/json" -d '{"items":[{"id_producto":1,"cantidad":2},{"id_producto":2,"cantidad":1}]}'
