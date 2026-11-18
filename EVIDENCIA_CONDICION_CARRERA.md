# Evidencia Documentada de la Condición de Carrera en el Inventario (Semana 9)

**Estudiante:** Andrés Hernández  
**Clase ejecutable:** `sifac.pruebas.DemoCondicionCarrera`  
**Clase del modelo protegida:** `sifac.comun.entidades.Producto` (`public synchronized void descontarExistencias(int cantidad)`)

---

## 1. Explicación Técnica del Problema

Cuando 30 hilos (cajas de la ferretería) intentan vender simultáneamente 1 unidad de un artículo del cual solo quedan **10 unidades** (`STOCK_INICIAL = 10`), la operación «verificar si `existencias >= cantidad` y luego restar `existencias -= cantidad`» **no es atómica**.

1. **Sin sincronización (`descontarSinSincronizar`):** Varios hilos entran al `if (existencias >= cantidad)` al mismo tiempo y leen que todavía quedan unidades antes de que los hilos anteriores actualicen el atributo `existencias`. Como consecuencia, se aprueban más ventas de las que realmente existen en bodega y el inventario termina en un **valor negativo inconsistente**.
2. **Con sincronización (`synchronized descontarExistencias`):** El monitor intrínseco del objeto `Producto` garantiza **exclusión mutua**. Mientras un hilo verifica y descuenta existencias dentro de la sección crítica, los demás hilos que intentan descontar el mismo producto pasan al estado `BLOCKED` y esperan su turno. Así, exactamente 10 hilos descuentan hasta llegar a `0` y los 20 restantes reciben `IllegalArgumentException("Existencias insuficientes...")`.

---

## 2. Salida Real Obtenida en Consola (`DemoCondicionCarrera`)

```text
======================================================================
   EVIDENCIA SEMANA 9: CONDICION DE CARRERA EN INVENTARIO SIFAC       
======================================================================

--- 1. PRUEBA SIN SINCRONIZAR (CONDICION DE CARRERA PROVOCADA) ---
Stock inicial del producto (TAL-01): 10
Cajas intentando comprar 1 unidad al mismo tiempo: 30
Ventas aprobadas:  30 (DEBIO SER MAXIMO 10)
Ventas rechazadas: 0
Existencias finales en inventario: -20  <-- INVENTARIO INCONSISTENTE (NEGATIVO)

--- 2. PRUEBA CON SYNCHRONIZED (PROBLEMA CORREGIDO) ---
Stock inicial del producto (TAL-01): 10
Cajas intentando comprar 1 unidad al mismo tiempo: 30
Ventas aprobadas:  10 (EXACTAMENTE 10)
Ventas rechazadas: 20 (Rechazadas por falta de stock)
Existencias finales en inventario: 0  <-- INVENTARIO CONSISTENTE