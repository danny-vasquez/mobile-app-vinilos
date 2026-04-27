# mobile-app-vinilos

## Ejecución de pruebas E2E (Espresso)

### Requisitos previos

- Android Studio instalado
- Emulador con **API 34 (Android 14)** — las pruebas no son compatibles con API 35 o superior debido a restricciones de hidden APIs en Espresso
- Conexión a internet activa (las pruebas consumen la API real)

### Crear el emulador API 34 (solo la primera vez)

1. En Android Studio: `Tools` → `Device Manager` → botón `+`
2. Elige un hardware (ej. Pixel 6) → `Next`
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