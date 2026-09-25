# IU Digital Radio

Aplicacion movil nativa para Android desarrollada en Kotlin que permite escuchar emisoras de radio colombianas en vivo por internet, personalizada con la identidad visual de la IU Digital de Antioquia.

Este proyecto corresponde a la Evidencia de Aprendizaje 3 de la asignatura Programacion de Dispositivos Moviles.

---

## Descripcion General

IU Digital Radio es una aplicacion diseñada para ofrecer una experiencia sencilla y moderna al momento de sintonizar estaciones de radio en tiempo real. 

La aplicacion se conecta a internet para obtener un listado actualizado de emisoras de Colombia con sus nombres, frecuencias y logos oficiales, permitiendo reproducir el sonido directamente desde el telefono con controles faciles de usar.

---

## Funcionalidades Principales

* Diseño Institucional: La interfaz utiliza los colores oficiales de la IU Digital de Antioquia (azul marino, rojo y amarillo).
* Foto de Perfil con Camara: Permite al usuario tomarse una foto en tiempo real usando la camara de su celular, previa autorizacion de permisos, mostrandola dentro de un marco circular personalizado.
* Reproductor en Vivo: Conecta con las señales de radio en directo, mostrando un disco estilo vinilo que gira mientras la musica suena, con opciones para pausar y silenciar el audio.
* Respuesta por Vibracion: El telefono emite una pequeña vibracion tactil cada vez que el usuario presiona los botones de control o selecciona una emisora del catalogo.
* Catalogo de Emisoras: Lista interactiva con estaciones de radio reales que se conecta a una base de datos publica en internet.
* Pantalla Adaptable: La aplicacion se ajusta automaticamente si el usuario coloca el celular en posicion vertical u horizontal, manteniendo la musica sonando de forma continua sin interrupciones.

---

## Herramientas y Tecnologias Utilizadas

* Lenguaje de programacion: Kotlin
* Diseño de interfaz: Jetpack Compose
* Motor de reproduccion de audio: Media3 ExoPlayer
* Carga de imagenes y logos: Coil
* Fuente de datos de emisoras: Radio Browser API
* Entorno de desarrollo: Android Studio

---

## Como Ejecutar el Proyecto

1. Clonar este repositorio o descargar los archivos del proyecto.
2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar a que el programa termine de sincronizar las dependencias con Gradle.
4. Conectar un dispositivo Android fisico con depuracion USB habilitada o iniciar un emulador.
5. Presionar el boton de Run (icono de reproducir) o la combinacion de teclas Shift + F10.

---

## Informacion del Proyecto

* Institucion: Institucion Universitaria Digital de Antioquia (IU Digital)
* Asignatura: Programacion de Dispositivos Moviles
* Modalidad: Trabajo Individual
* Desarrollador: Juan Pablo Yara Cifuentes
* Año: 2026