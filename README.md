# SIFAC — Sistema de Facturación para Ferretería (`sifac_andres`)

## Semana 11 (Laboratorio 10): Consulta al Padrón y Confirmación al Central por Sockets

### Orden de Arranque y Puertos
1. **Servidor de Padrón (`sifac.padron.servidor.ServidorConcurrente`)**
   - Puerto TCP: `5051` (flujos de red en `UTF-8`, lectura de archivos del TSE en `ISO-8859-15`).
2. **Servidor Central de Demostración (`sifac.central.CentralDemo`)**
   - Puerto TCP: `5052` (protocolo `REGISTRAR|id|monto` con respuestas `ACK|id` y `CONFLICTO|id`).
3. **Cliente de Caja (`sifac.caja.ui.ConsolaCaja`)**
   - Inyecta `RepositorioClientesPadron("localhost", 5051)` en `ServicioFacturacion` sin alterar la capa lógica.

### Comandos de Ejecución y Verificación
Desde NetBeans (**Run File** `Shift + F6`) o por terminal desde la raíz del proyecto:

```bash
# 1. Iniciar el servidor concurrente de padrón (Puerto 5051)
java -cp build/classes sifac.padron.servidor.ServidorConcurrente

# 2. Ejecutar la Consola de Caja (menú interactivo o consulta por argumentos)
java -cp build/classes sifac.caja.ui.ConsolaCaja localhost 5051 104560789
java -cp build/classes sifac.caja.ui.ConsolaCaja localhost 5051 999999999

# 3. Ejecutar las 8 verificaciones automatizadas de fallos de red y protocolo
java -cp build/classes sifac.pruebas.VerificarPadron

# 4. Iniciar CentralDemo (Puerto 5052) y probar envío con confirmación ACK y conflicto
java -cp build/classes sifac.central.CentralDemo
java -cp build/classes sifac.caja.ui.EnviarDocumento
