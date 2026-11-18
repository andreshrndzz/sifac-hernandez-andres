# SIFAC — Sistema de Facturación para Ferretería (`sifac_andres`)

**Curso:** BIS06 · Programación III — Universidad Latina de Costa Rica  
**Estudiante:** Andrés Hernández  
**Docente:** Bryan Alejandro Vega Rondón  
**Requisitos:** Java SE 17+, Apache NetBeans, JUnit 4/5

---

## 1. Arquitectura en Capas y Estructura de Paquetes

```text
src/sifac/
├── comun/
│   ├── entidades/      Documento (abstracta), FacturaContado, FacturaCredito,
│   │                   NotaCredito, Proforma, LineaDetalle, Producto, Cliente,
│   │                   Cajero, Sucursal, TipoCambio
│   ├── estructuras/    NodoEstado, HistorialEstados (S3-S4), NodoLinea, ListaLineas (S4/Tarea 1),
│   │                   PilaOperaciones (S5), NodoDoc, ColaEnvio (S5/S10)
│   ├── datos/          RepositorioClientes, RepositorioProductos (interfaces),
│   │                   RepositorioClientesMemoria, RepositorioProductosMemoria,
│   │                   RepositorioClientesPadron, ProtocoloPadron, ServicioPadronException (S11)
│   └── logica/         ServicioFacturacion (reglas de negocio, cero System.out)
├── caja/ui/            ConsolaCaja (menú de los 9 requisitos funcionales), EnviarDocumento
├── cliente/presentacion/ ConsolaTipoCambio (módulo de tipo de cambio BCCR / simulado en memoria)
├── servidor/           RepositorioTipoCambio, TipoCambioEnMemoria, TipoCambioBCCR, ServicioTipoCambio
├── padron/             ServidorConcurrente (puerto 5051), AtencionCliente, RepositorioPadron, ClienteMasivo
├── central/            CentralDemo (puerto 5052, confirmación ACK e idempotencia)
└── pruebas/            DemoCondicionCarrera (Semana 9) y VerificarPadron (8 verificaciones Semana 11)