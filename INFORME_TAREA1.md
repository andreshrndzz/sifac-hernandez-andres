# Informe Tarea 1 — Operaciones avanzadas sobre la lista enlazada


## 1. ¿Por qué invertir se resuelve reapuntando flechas y no creando una lista nueva y copiando?

Crear una lista nueva duplica la memoria consumida: mientras la nueva se construye, la original sigue existiendo con todos sus nodos. En una factura con muchas líneas, eso es un desperdicio que se evita reapuntando.

Pero el argumento más importante no es la memoria: es la **integridad de las referencias**. Otros objetos del sistema (el documento, el servicio de facturación) pueden tener punteros a los nodos originales. Si se crea una lista nueva con nodos nuevos, esas referencias quedan apuntando a nodos huérfanos que ya no forman parte de la lista activa. Reapuntando las flechas de los mismos nodos, todas las referencias existentes siguen siendo válidas: los nodos son los mismos, solo cambió el orden en que están enlazados.

## 2. En consolidar, ¿qué problema aparece al eliminar un nodo mientras se recorre la lista, y cómo lo resolvió?

El problema es que al eliminar un nodo (el duplicado), la referencia `interno` queda apuntando a un nodo ya desenganchado de la cadena. Si se hace `interno = interno.getSiguiente()` después de eliminarlo, se avanza desde un nodo que ya no pertenece a la lista, y se pierden los nodos que siguen.

La solución fue **no avanzar `anterior` cuando se elimina**: al desengancharse el nodo duplicado, `anterior.getSiguiente()` ya apunta al nodo que sigue al eliminado. Entonces `interno` se reasigna con `anterior.getSiguiente()` en vez de `interno.getSiguiente()`. De esa forma, `anterior` se mantiene bien posicionado y el recorrido continúa correctamente por la cadena vigente.

## 3. ¿Cuál de las cuatro operaciones sería más barata si la lista fuera doblemente enlazada, y por qué?

**Invertir** sería la que más se beneficia. En una lista simple, invertir requiere recorrer todos los nodos con tres referencias para reapuntar cada flecha `siguiente` hacia atrás, porque no existe flecha `anterior`. En una lista doblemente enlazada, cada nodo ya tiene referencia al anterior y al siguiente. Invertir se reduciría a intercambiar las dos referencias de cada nodo (`siguiente` pasa a ser `anterior` y viceversa) y luego intercambiar la cabeza con la cola. La lógica es más directa porque no se necesita "guardar antes de perder", ya que el nodo siempre conoce a sus dos vecinos.

Consolidar también se beneficiaría parcialmente, porque al eliminar un nodo interno se puede acceder al anterior en O(1) sin necesidad de llevar una referencia extra durante el recorrido. Pero el beneficio es menor que en invertir, que es donde la ausencia del enlace hacia atrás más se nota.
