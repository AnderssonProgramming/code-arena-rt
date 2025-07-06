# 🎯 Code Arena: Multiplayer Puzzle Battle

**Autor:** [Tu Nombre]  
**Institución:** Escuela Colombiana de Ingeniería  
**Curso:** Arquitecturas de Software – ARSW  
**Período:** 2024-I

---

## 📝 Resumen

**Code Arena** es una plataforma web de competencias en tiempo real donde múltiples jugadores resuelven acertijos de lógica, rompecabezas y desafíos de programación en batallas multijugador. Los usuarios pueden crear salas privadas o unirse a partidas públicas para competir contra otros jugadores en tiempo real, con un sistema de puntajes, rankings y mecánicas de colaboración/sabotaje que hacen cada partida única y emocionante.

La plataforma combina elementos de gamificación con entrenamiento mental, creando un entorno competitivo similar a plataformas como LeetCode o HackerRank, pero con un enfoque más lúdico y social.

---

## 🎮 Descripción del Proyecto

### Antecedentes

En la era digital actual, las plataformas de entrenamiento mental y competencias de programación han ganado popularidad significativa. Plataformas como:

- **LeetCode**: Enfocada en problemas algorítmicos individuales
- **CodeSignal**: Evaluaciones técnicas y competencias
- **CodinGame**: Batallas de bots programados
- **HackerRank**: Desafíos de programación y certificaciones

Sin embargo, estas plataformas presentan limitaciones:

1. **Falta de interacción en tiempo real** entre competidores
2. **Experiencia predominantemente individual** sin elementos colaborativos
3. **Interfaz académica** poco atractiva para usuarios casuales
4. **Ausencia de mecánicas de juego** que generen engagement

### Problema que se Resuelve

**Code Arena** aborda estas limitaciones ofreciendo:

✅ **Competencia en tiempo real** con múltiples jugadores simultáneos  
✅ **Mecánicas de colaboración y sabotaje** que añaden estrategia  
✅ **Interfaz gamificada** tipo battle royale para desarrolladores  
✅ **Diversidad de retos** (lógica, matemáticas, pseudocódigo, patrones)  
✅ **Sistema social** con salas, chat, rankings y estadísticas  

### Arquitectura del Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Database     │
│   (Angular)     │◄──►│ (Spring Boot)   │◄──►│   (MongoDB)     │
│                 │    │                 │    │                 │
│ • UI/UX         │    │ • REST APIs     │    │ • Usuarios      │
│ • WebSocket     │    │ • WebSocket     │    │ • Partidas      │
│ • Estado Global │    │ • Lógica Juego  │    │ • Estadísticas  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Valor Agregado Único

**Code Arena** se diferencia de la competencia por:

🎯 **Experiencia Multijugador Real**: Batallas síncronas con hasta 8 jugadores  
🎲 **Mecánicas de Sabotaje**: Los jugadores pueden interferir estratégicamente  
🏆 **Gamificación Completa**: Niveles, logros, rankings y temporadas  
🌐 **Accesibilidad**: Retos para todos los niveles, no solo programadores expertos  
📊 **Analytics Avanzados**: Estadísticas detalladas de rendimiento y progreso

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Justificación |
|------------|------------|---------------|
| **Frontend** | Angular 20 + TypeScript | Framework robusto, componentes reutilizables |
| **Backend** | Spring Boot 3.5 + Kotlin | Ecosistema maduro, soporte WebSocket nativo |
| **Base de Datos** | MongoDB | Esquemas flexibles, escalabilidad horizontal |
| **Tiempo Real** | WebSocket + STOMP | Comunicación bidireccional eficiente |
| **Deploy** | Docker + Azure | Containerización y escalabilidad cloud |

---

## 🎯 Funcionalidades Principales

### 🔥 Core Features (MVP)

1. **Sistema de Autenticación**
   - Registro/Login de usuarios
   - Perfiles personalizables
   - Gestión de sesiones

2. **Lobby y Matchmaking**
   - Crear salas privadas/públicas
   - Unirse a partidas existentes
   - Sistema de invitaciones

3. **Motor de Juego en Tiempo Real**
   - Sincronización de estado entre jugadores
   - Timer compartido
   - Validación de respuestas en tiempo real

4. **Tipos de Retos**
   - Rompecabezas lógicos (Sudoku, patrones)
   - Desafíos de pseudocódigo
   - Problemas matemáticos
   - Trivia técnica

5. **Sistema de Puntajes**
   - Cálculo dinámico basado en tiempo/precisión
   - Rankings globales y por temporada
   - Estadísticas personales

### 🚀 Advanced Features (Post-MVP)

- Chat en tiempo real durante partidas
- Mecánicas de sabotaje (bloquear rival por 5 segundos)
- Modo colaborativo (equipos de 2-4 jugadores)
- Torneos programados
- Sistema de logros y badges
- Replay de partidas
- APIs para integraciones externas

---

## 📊 Beneficios del Proyecto

### Para Usuarios
- **Entretenimiento educativo** combinando diversión y aprendizaje
- **Desarrollo de habilidades** lógicas y de resolución de problemas
- **Competencia sana** con ranking y reconocimientos
- **Flexibilidad** para jugar solo o en grupo

### Para la Industria
- **Herramienta de reclutamiento** para empresas tech
- **Plataforma de capacitación** para equipos de desarrollo
- **Comunidad de práctica** para desarrolladores

### Técnicos
- **Demostración de arquitecturas modernas** (microservicios, tiempo real)
- **Implementación de patrones avanzados** (CQRS, Event Sourcing)
- **Escalabilidad horizontal** para múltiples usuarios concurrentes

---

## 🎨 Mockups y Diagramas

### Flujo Principal de Usuario
```
Usuario → Registro/Login → Lobby → Seleccionar Sala → Batalla en Tiempo Real → Resultados → Estadísticas
```

### Arquitectura de Componentes
```
┌────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular)                  │
├────────────────────────────────────────────────────────┤
│ Auth Service │ Game Service │ Socket Service │ UI Comp. │
└────────────────────────────────────────────────────────┘
                              ↕ HTTP/WebSocket
┌────────────────────────────────────────────────────────┐
│                  BACKEND (Spring Boot)                 │
├────────────────────────────────────────────────────────┤
│ Auth API │ Game Engine │ WebSocket Handler │ User Mgmt │
└────────────────────────────────────────────────────────┘
                              ↕ TCP
┌────────────────────────────────────────────────────────┐
│                    DATABASE (MongoDB)                  │
├────────────────────────────────────────────────────────┤
│   Users   │   Games   │   Scores   │   Analytics       │
└────────────────────────────────────────────────────────┘
```

---

## 📈 Roadmap de Desarrollo

### Sprint 1: Definición ✅
- [x] Propuesta documentada
- [x] Historias de usuario
- [x] Arquitectura inicial

### Sprint 2: MVP Backend
- [ ] API REST básica
- [ ] WebSocket para tiempo real
- [ ] Base de datos MongoDB
- [ ] Sistema de autenticación

### Sprint 3: Frontend & Integración
- [ ] Interfaz Angular
- [ ] Integración completa
- [ ] Testing y deploy
- [ ] Documentación final

---

*Proyecto desarrollado como parte del curso Arquitecturas de Software (ARSW) - Escuela Colombiana de Ingeniería*
