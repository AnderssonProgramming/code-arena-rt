# 📖 Historias de Usuario - Code Arena

## 🎯 Principios INVEST Aplicados

Todas las historias de usuario siguen los principios **INVEST**:
- **I**ndependent (Independiente)
- **N**egotiable (Negociable)
- **V**aluable (Valiosa)
- **E**stimable (Estimable)
- **S**mall (Pequeña)
- **T**estable (Verificable)

---

## 👤 Épicas y Personas

### 🎮 Personas Identificadas

**1. Gamer Competitivo (Lucas)**
- Edad: 22-28 años
- Busca desafíos intelectuales y ranking
- Quiere competir contra otros jugadores en tiempo real

**2. Desarrollador en Formación (Ana)**
- Edad: 20-25 años
- Estudiante de ingeniería de sistemas
- Busca mejorar habilidades lógicas de forma divertida

**3. Reclutador Tech (Carlos)**
- Edad: 30-40 años
- HR en empresa tecnológica
- Busca herramientas para evaluar candidatos

---

## 🎯 Épica 1: Gestión de Usuarios

### US-001: Registro de Usuario

**Como** un nuevo usuario  
**Quiero** crear una cuenta en la plataforma  
**Para** poder acceder a las funcionalidades de Code Arena

#### Criterios de Aceptación:
- [ ] El sistema permite registro con email y contraseña
- [ ] La contraseña debe tener mínimo 8 caracteres con mayúsculas, minúsculas y números
- [ ] El email debe ser único en el sistema
- [ ] Se envía email de confirmación tras registro exitoso
- [ ] El usuario puede elegir un nickname único
- [ ] Los campos obligatorios están claramente marcados
- [ ] Se muestran mensajes de error claros para datos inválidos

#### Definición de Terminado:
- Backend valida datos y almacena en BD
- Frontend muestra formulario responsive
- Emails de confirmación funcionales
- Tests unitarios y de integración

**Estimación:** 5 puntos  
**Prioridad:** Alta

---

### US-002: Inicio de Sesión

**Como** un usuario registrado  
**Quiero** iniciar sesión en la plataforma  
**Para** acceder a mi perfil y participar en partidas

#### Criterios de Aceptación:
- [ ] Login con email/username y contraseña
- [ ] Opción "Recordar sesión" por 30 días
- [ ] Enlace "Olvidé mi contraseña" funcional
- [ ] Redirección automática a lobby tras login exitoso
- [ ] Mensaje de error claro para credenciales incorrectas
- [ ] Bloqueo temporal tras 5 intentos fallidos
- [ ] Logout desde cualquier página

#### Definición de Terminado:
- JWT tokens implementados
- Sesiones seguras con expiración
- Tests de seguridad pasando

**Estimación:** 3 puntos  
**Prioridad:** Alta

---

### US-003: Perfil de Usuario

**Como** un usuario autenticado  
**Quiero** ver y editar mi perfil  
**Para** personalizar mi experiencia y ver mis estadísticas

#### Criterios de Aceptación:
- [ ] Visualizar nickname, email, fecha registro, estadísticas
- [ ] Editar nickname (si no está en uso)
- [ ] Cambiar contraseña con confirmación de actual
- [ ] Ver historial de partidas jugadas
- [ ] Mostrar ranking actual y mejores puntajes
- [ ] Avatar personalizable (opcional)
- [ ] Configuración de notificaciones

#### Definición de Terminado:
- CRUD completo de perfiles
- Validaciones frontend y backend
- Dashboard responsivo

**Estimación:** 8 puntos  
**Prioridad:** Media

---

## 🎮 Épica 2: Sistema de Partidas

### US-004: Crear Sala de Juego

**Como** un usuario autenticado  
**Quiero** crear una sala de juego  
**Para** invitar amigos o esperar que otros se unan

#### Criterios de Aceptación:
- [ ] Formulario para crear sala con nombre personalizado
- [ ] Configurar número máximo de jugadores (2-8)
- [ ] Elegir dificultad del reto (Fácil, Medio, Difícil)
- [ ] Sala pública (visible para todos) o privada (solo con código)
- [ ] Generar código único de 6 dígitos para salas privadas
- [ ] El creador es automáticamente el host/admin
- [ ] Host puede expulsar jugadores antes de iniciar
- [ ] Cancelar sala si no hay suficientes jugadores en 5 minutos

#### Definición de Terminado:
- WebSocket para actualizaciones en tiempo real
- Lógica de salas en backend
- UI intuítiva para configuración

**Estimación:** 13 puntos  
**Prioridad:** Alta

---

### US-005: Unirse a Sala Existente

**Como** un usuario autenticado  
**Quiero** unirme a una sala de juego existente  
**Para** participar en una partida con otros jugadores

#### Criterios de Aceptación:
- [ ] Lista de salas públicas disponibles con información básica
- [ ] Filtros por dificultad y número de jugadores
- [ ] Unirse con código de sala privada
- [ ] Verificar que la sala no esté llena o iniciada
- [ ] Mostrar jugadores actuales en la sala
- [ ] Salir de la sala antes de que inicie el juego
- [ ] Notificación cuando la sala está lista para comenzar
- [ ] Chat básico en sala de espera

#### Definición de Terminado:
- Lista en tiempo real de salas
- Validaciones de capacidad y estado
- Chat funcional pre-partida

**Estimación:** 8 puntos  
**Prioridad:** Alta

---

### US-006: Motor de Juego en Tiempo Real

**Como** un jugador en una partida activa  
**Quiero** resolver retos en tiempo real contra otros  
**Para** competir y demostrar mis habilidades

#### Criterios de Aceptación:
- [ ] Timer visible compartido para todos los jugadores
- [ ] Reto mostrado simultáneamente a todos
- [ ] Envío de respuesta con validación inmediata
- [ ] Actualización en tiempo real del progreso de rivales
- [ ] Sistema de puntaje basado en tiempo y precisión
- [ ] Indicador visual de quién va ganando
- [ ] Posibilidad de rendirse (abandonar partida)
- [ ] Conexión estable sin pérdida de sincronización

#### Definición de Terminado:
- WebSocket optimizado para baja latencia
- Algoritmos de validación eficientes
- Estado sincronizado entre todos los clientes

**Estimación:** 21 puntos  
**Prioridad:** Crítica

---

## 🧩 Épica 3: Contenido y Retos

### US-007: Banco de Retos

**Como** administrador del sistema  
**Quiero** gestionar un banco de retos variados  
**Para** ofrecer contenido diverso y desafiante

#### Criterios de Aceptación:
- [ ] CRUD completo para retos (crear, leer, actualizar, eliminar)
- [ ] Categorías: Lógica, Matemáticas, Patrones, Pseudocódigo
- [ ] Niveles de dificultad con puntajes base diferentes
- [ ] Retos con múltiples opciones o respuesta abierta
- [ ] Tiempo límite configurable por reto
- [ ] Preview del reto antes de activarlo
- [ ] Estadísticas de dificultad real basadas en jugadores
- [ ] Importar retos desde archivo JSON/CSV

#### Definición de Terminado:
- Panel admin funcional
- Base de datos con 50+ retos iniciales
- API REST para gestión de contenido

**Estimación:** 13 puntos  
**Prioridad:** Media

---

### US-008: Algoritmo de Selección de Retos

**Como** jugador en una partida  
**Quiero** que el sistema elija retos apropiados  
**Para** tener una experiencia equilibrada y justa

#### Criterios de Aceptación:
- [ ] Selección aleatoria dentro del nivel elegido
- [ ] Evitar repetir retos recientes para el mismo usuario
- [ ] Balancear tipos de reto en partidas largas
- [ ] Ajustar dificultad basado en rendimiento histórico del jugador
- [ ] Retos especiales para partidas de más de 4 jugadores
- [ ] Preview de 3 segundos antes de mostrar reto completo
- [ ] Fallback a retos predeterminados si falla la selección

#### Definición de Terminado:
- Algoritmo optimizado y testeable
- Configuración flexible de reglas
- Logs detallados para análisis

**Estimación:** 8 puntos  
**Prioridad:** Media

---

## 📊 Épica 4: Sistema de Puntuación y Rankings

### US-009: Cálculo de Puntajes

**Como** jugador  
**Quiero** un sistema de puntaje justo y transparente  
**Para** entender mi rendimiento y progresar competitivamente

#### Criterios de Aceptación:
- [ ] Puntaje base por dificultad del reto
- [ ] Bonificación por tiempo (más rápido = más puntos)
- [ ] Multiplicador por posición final (1er lugar, 2do, etc.)
- [ ] Penalización por respuestas incorrectas
- [ ] Bonus por racha de respuestas correctas
- [ ] Mostrar desglose de puntaje al final de cada reto
- [ ] Puntaje total acumulativo visible durante la partida
- [ ] Sistema de ELO para ranking competitivo a largo plazo

#### Definición de Terminado:
- Fórmulas matemáticas documentadas y testeadas
- UI clara para mostrar cálculos
- Balance testeado con usuarios reales

**Estimación:** 5 puntos  
**Prioridad:** Alta

---

### US-010: Rankings y Estadísticas

**Como** jugador competitivo  
**Quiero** ver rankings globales y mis estadísticas  
**Para** medir mi progreso y comparar con otros

#### Criterios de Aceptación:
- [ ] Ranking global top 100 actualizado en tiempo real
- [ ] Ranking semanal/mensual/por temporada
- [ ] Estadísticas personales: partidas jugadas, % victorias, tiempo promedio
- [ ] Gráficos de progreso histórico
- [ ] Comparación con amigos/jugadores favoritos
- [ ] Filtros por tipo de reto y dificultad
- [ ] Badges/logros por hitos alcanzados
- [ ] Exportar estadísticas a PDF/Excel

#### Definición de Terminado:
- Dashboard analítico completo
- Consultas optimizadas para rendimiento
- Visualizaciones atractivas e informativas

**Estimación:** 13 puntos  
**Prioridad:** Media

---

## 🔧 Épica 5: Funcionalidades Técnicas

### US-011: Notificaciones en Tiempo Real

**Como** usuario de la plataforma  
**Quiero** recibir notificaciones relevantes  
**Para** estar informado de eventos importantes

#### Criterios de Aceptación:
- [ ] Notificación cuando un amigo se conecta
- [ ] Alerta cuando una sala que me interesa tiene cupo
- [ ] Notificación de inicio de partida
- [ ] Avisos de desconexión/reconexión durante juego
- [ ] Configurar qué notificaciones recibir
- [ ] Notificaciones push opcionales (navegador)
- [ ] Historial de notificaciones recientes
- [ ] Sonidos opcionales para eventos críticos

#### Definición de Terminado:
- Sistema de notificaciones escalable
- Configuraciones granulares
- UX no intrusiva

**Estimación:** 8 puntos  
**Prioridad:** Baja

---

### US-012: Reconexión Automática

**Como** jugador en partida activa  
**Quiero** reconectarme automáticamente si pierdo conexión  
**Para** no perder progreso ni afectar a otros jugadores

#### Criterios de Aceptación:
- [ ] Detección automática de pérdida de conexión
- [ ] Intentos de reconexión cada 2 segundos (máximo 10)
- [ ] Mantener estado de partida durante desconexión temporal
- [ ] Pausar timer si más del 50% se desconecta simultáneamente
- [ ] Mostrar indicador visual de estado de conexión
- [ ] Sincronización completa al reconectar
- [ ] Timeout de 30 segundos antes de marcar jugador como abandonado
- [ ] Opción manual de reconexión

#### Definición de Terminado:
- Resilencia ante fallos de red
- Estado persistente robusto
- Tests de conectividad automatizados

**Estimación:** 13 puntos  
**Prioridad:** Alta

---

## 📋 Resumen de Historias

| ID | Historia | Estimación | Prioridad | Sprint |
|----|----------|------------|-----------|---------|
| US-001 | Registro de Usuario | 5 | Alta | 2 |
| US-002 | Inicio de Sesión | 3 | Alta | 2 |
| US-003 | Perfil de Usuario | 8 | Media | 3 |
| US-004 | Crear Sala de Juego | 13 | Alta | 2 |
| US-005 | Unirse a Sala | 8 | Alta | 2 |
| US-006 | Motor de Juego RT | 21 | Crítica | 2-3 |
| US-007 | Banco de Retos | 13 | Media | 2 |
| US-008 | Selección de Retos | 8 | Media | 3 |
| US-009 | Cálculo Puntajes | 5 | Alta | 2 |
| US-010 | Rankings | 13 | Media | 3 |
| US-011 | Notificaciones | 8 | Baja | 3 |
| US-012 | Reconexión | 13 | Alta | 3 |

**Total de Puntos de Historia:** 117  
**Sprint 2 (MVP):** ~60 puntos  
**Sprint 3 (Completo):** ~57 puntos

---

*Historias de usuario desarrolladas siguiendo metodología Scrum y principios INVEST para Code Arena - Escuela Colombiana de Ingeniería*
