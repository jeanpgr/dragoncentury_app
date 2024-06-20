# DragonDentury App

Es una aplicación móvil para Android desarrollada en Kotlin que permite registrar reportes de ventas diarias, buscar reportes específicos por fecha o en un rango de fechas, y obtener un reporte de ventas y gastos totales en un rango de fechas dado.

## Características

- **Registro de Reportes de Venta**: Permite registrar reportes de venta diarios con detalles como productos vendidos, cantidad, precio, y gastos.
- **Búsqueda de Reportes**: Busca reportes de ventas específicos por fecha o dentro de un rango de fechas.
- **Reporte de Ventas y Gastos Totales**: Genera un reporte de ventas y gastos totales dentro de un rango de fechas especificado por el usuario.

## Requisitos

- Android Studio 4.1 o superior
- Kotlin 1.4 o superior
- Dispositivo o emulador con Android 5.0 (Lollipop) o superior

## Instalación

1. Clona el repositorio:

    ```bash
    git clone https://github.com/jeanpgr/dragoncentury_app.git
    ```

2. Abre el proyecto en Android Studio.

3. Sincroniza el proyecto con Gradle.

4. Ejecuta la aplicación en un dispositivo o emulador.

## Arquitectura

La aplicación sigue el patrón de arquitectura MVVM (Model-View-ViewModel) para mantener un código limpio y manejable.

### Paquetes Principales

- `model`: Contiene las clases de datos y la lógica de negocio.
- `view`: Contiene las actividades y fragmentos.
- `viewmodel`: Contiene los ViewModels que gestionan los datos para las vistas.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m 'Agregar nueva funcionalidad'`).
4. Envía tus cambios a la rama principal (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo `LICENSE` para obtener más información.

## Contacto

Para cualquier consulta o sugerencia, por favor contacta a [Jean Pierre] en [jean_0720@hotmail.com].
