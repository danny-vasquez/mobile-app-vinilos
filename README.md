# mobile-app-vinilos

## Construcción y Ejecución Local

Las instrucciones en el README del repositorio son suficientes para construir la aplicación de forma local:

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar el proyecto con los archivos de Gradle.
4. Ejecutar la aplicación (`Run 'app'`) en un emulador o dispositivo físico (se recomienda usar un emulador con **API 34**).

Para generar el APK de depuración desde la terminal:
```bash
./gradlew assembleDebug
```
El archivo APK generado se encontrará en la raiz delk proyecto.

## Ejecución de pruebas E2E (Espresso)

### Requisitos previos

- Android Studio instalado
- Emulador con **API 34 (Android 14)** — las pruebas no son compatibles con API 35 o superior debido a restricciones de hidden APIs en Espresso
- Conexión a internet activa (las pruebas consumen la API real)

### Crear el emulador API 34 (solo la primera vez)

1. En Android Studio: `Tools` → `Device Manager` → botón `+`
2. Elige un hardware (ej. Pixel 4 API 34) → `Next`
3. En la pestaña **Release Name**, selecciona **UpsideDownCake · API 34 · x86_64**
   - Si aparece un ícono de descarga, descárgalo primero
4. `Next` → `Finish`

### Correr las pruebas

**Desde Android Studio:**

1. Selecciona el emulador API 34 en el selector de dispositivo (barra superior)
2. Inícialo si no está corriendo
3. Click derecho sobre la carpeta `app/src/androidTest` → `Run 'All Tests'`

O ejecuta una clase específica:
- Click derecho sobre `AlbumsListE2ETest` → `Run`
- Click derecho sobre `AlbumDetailE2ETest` → `Run`
- Click derecho sobre `NavigationE2ETest` → `Run`

**Desde terminal (con emulador API 34 activo):**

```bash
./gradlew connectedAndroidTest
```

Los resultados quedan en:
```
app/build/reports/androidTests/connected/index.html
```

### Pruebas incluidas

| Clase | Descripción | Tests |
|---|---|---|
| `AlbumsListE2ETest` | Pantalla de lista de álbumes | 5 |
| `AlbumDetailE2ETest` | Pantalla de detalle de álbum | 8 |
| `NavigationE2ETest` | Flujo de navegación completo | 3 |
