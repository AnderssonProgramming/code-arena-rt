# 🎯 Code Arena: Multiplayer Puzzle Battle

> **Plataforma de competencias en tiempo real** donde múltiples jugadores resuelven acertijos de lógica en batallas multijugador

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![Angular](https://img.shields.io/badge/Angular-20.0-red.svg)](https://angular.io/)

---

## 📋 Descripción del Proyecto

**Code Arena** es una aplicación web de colaboración en tiempo real que permite a múltiples usuarios competir simultáneamente resolviendo rompecabezas, acertijos de lógica y desafíos de programación. La plataforma combina elementos de gamificación con entrenamiento mental, creando un entorno competitivo y social único.

### 🎮 Características Principales

- ⚡ **Tiempo Real**: Competencias síncronas con WebSocket
- 👥 **Multijugador**: Hasta 8 jugadores por sala
- 🧩 **Diversos Retos**: Lógica, matemáticas, patrones, pseudocódigo
- 🏆 **Sistema de Ranking**: Puntajes, estadísticas y clasificaciones
- 🎯 **Mecánicas Únicas**: Colaboración y sabotaje estratégico
- 📱 **Responsive**: Funciona en desktop, tablet y móvil

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Database     │
│   (Angular 20)  │◄──►│ (Spring Boot)   │◄──►│   (MongoDB)     │
│                 │    │                 │    │                 │
│ • Componentes   │    │ • REST APIs     │    │ • Usuarios      │
│ • WebSocket     │    │ • WebSocket     │    │ • Partidas      │
│ • Estado Global │    │ • Game Engine   │    │ • Retos         │
│ • TypeScript    │    │ • Kotlin        │    │ • Estadísticas  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🛠️ Stack Tecnológico

### Backend
- **Framework**: Spring Boot 3.5.3
- **Lenguaje**: Kotlin 1.9.25
- **Base de Datos**: MongoDB 7.0
- **Comunicación RT**: WebSocket + STOMP
- **Seguridad**: Spring Security + JWT
- **Build Tool**: Gradle 8.14.2

### Frontend
- **Framework**: Angular 20.0
- **Lenguaje**: TypeScript 5.8
- **Comunicación**: Socket.IO Client
- **UI**: Angular Material + Tailwind CSS
- **Build Tool**: Angular CLI

### DevOps & Deploy
- **Containerización**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Cloud**: Azure/AWS
- **Monitoreo**: Spring Actuator

---

## 📚 Documentación del Proyecto

### 📋 Sprint 1 - Definición (Completado)

1. **[📝 Propuesta del Proyecto](./docs/proyecto-propuesta.md)**
   - Resumen ejecutivo y descripción detallada
   - Análisis de competencia y valor agregado
   - Arquitectura del sistema y stack tecnológico

2. **[📖 Historias de Usuario](./docs/historias-usuario.md)**
   - 12 historias de usuario siguiendo principios INVEST
   - Criterios de aceptación detallados
   - Estimaciones y priorización para Sprints 2-3

3. **[🏗️ Arquitectura del Sistema](./docs/arquitectura.md)**
   - Diseño de alto nivel y patrones aplicados
   - Modelo de datos y flujos de comunicación
   - Estrategias de escalabilidad y seguridad

### 🔄 Próximos Sprints

- **Sprint 2**: Implementación del MVP Backend
- **Sprint 3**: Frontend e Integración Completa

---

## 🚀 Guía de Inicio Rápido

### Prerrequisitos

- **Java 21+** (o 23)
- **Node.js 18+**
- **MongoDB 7.0+**
- **Git**

### Configuración del Ambiente

#### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/code-arena-rt.git
cd code-arena-rt
```

#### 2. Backend (Spring Boot)
```bash
# Construir el proyecto
./gradlew build

# Ejecutar tests
./gradlew test

# Iniciar aplicación
./gradlew bootRun
```

#### 3. Frontend (Angular)
```bash
cd ../code-arena-rt-front

# Instalar dependencias
npm install

# Desarrollo
npm start

# Build producción
npm run build
```

#### 4. Base de Datos (MongoDB)
```bash
# Con Docker
docker run -d -p 27017:27017 --name mongodb mongo:7

# O instalación local
# Ver documentación oficial de MongoDB
```

---

## 🎯 Funcionalidades Planificadas

### 🔥 Sprint 2 - MVP Backend
- [x] Configuración base del proyecto
- [ ] Sistema de autenticación JWT
- [ ] API REST para gestión de usuarios
- [ ] WebSocket para comunicación en tiempo real
- [ ] Motor de juego básico
- [ ] Base de datos MongoDB configurada
- [ ] Banco inicial de retos

### 🚀 Sprint 3 - Frontend & Integración
- [ ] Interfaz de usuario Angular
- [ ] Componentes de autenticación
- [ ] Lobby de salas en tiempo real
- [ ] Motor de juego frontend
- [ ] Sistema de puntajes y rankings
- [ ] Integración completa frontend-backend
- [ ] Deploy en cloud

### 🌟 Funcionalidades Futuras
- [ ] Chat en tiempo real durante partidas
- [ ] Mecánicas de sabotaje entre jugadores
- [ ] Modo colaborativo por equipos
- [ ] Torneos programados
- [ ] Sistema de logros y badges
- [ ] Replay de partidas
- [ ] APIs públicas para integraciones

---

## 🤝 Contribución

Este es un proyecto académico individual para el curso **Arquitecturas de Software (ARSW)** de la Escuela Colombiana de Ingeniería.

### Metodología de Desarrollo

- **Framework**: Scrum con 3 Sprints
- **Duración**: Sprint de 3-4 semanas cada uno
- **Planificación**: Historias de usuario y estimación por puntos
- **Calidad**: TDD, Code Review, Documentación continua

---

## 📊 Estado del Proyecto

| Sprint | Estado | Progreso | Fecha Objetivo |
|--------|--------|----------|----------------|
| Sprint 1 | ✅ Completado | 100% | ✅ Semana 4 |
| Sprint 2 | 🔄 En Progreso | 10% | 📅 Semana 8 |
| Sprint 3 | ⏳ Planificado | 0% | 📅 Semana 12 |

### Métricas de Desarrollo

- **Historias de Usuario**: 12 definidas
- **Puntos de Historia**: 117 total
- **Líneas de Código**: ~500 (inicial)
- **Cobertura de Tests**: Objetivo 80%+
- **Documentación**: 100% actualizada

---

## 📞 Información del Proyecto

**Institución**: Escuela Colombiana de Ingeniería  
**Programa**: Arquitecturas de Software (ARSW)  
**Período**: 2024-I  
**Modalidad**: Proyecto Individual  

### Objetivos de Aprendizaje

1. **Aplicar arquitecturas distribuidas** modernas
2. **Implementar comunicación en tiempo real** con WebSocket
3. **Diseñar sistemas escalables** y tolerantes a fallos
4. **Desarrollar aplicaciones full-stack** con tecnologías actuales
5. **Aplicar metodologías ágiles** (Scrum) en desarrollo

---

## 📄 Licencia

Este proyecto es desarrollado con fines académicos para la Escuela Colombiana de Ingeniería.

---

## 🔗 Enlaces Importantes

- [📚 Documentación Completa](./docs/)
- [🎯 Backlog del Proyecto](./docs/historias-usuario.md)
- [🏗️ Diagramas de Arquitectura](./docs/arquitectura.md)
- [📝 Propuesta Detallada](./docs/proyecto-propuesta.md)

---

*Desarrollado con ❤️ para ARSW - ECI 2024-I*