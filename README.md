# MMRC Android Player

Нативный медиаплеер для Android TV и киосков. Подключается к MMRC серверу через Socket.IO и воспроизводит медиаконтент в реальном времени.

## Возможности

- Видео (MP4, WebM, MKV, MOV, AVI)
- Аудио (MP3, AAC, WAV, FLAC, OGG, M4A)
- Изображения (PNG, JPG, JPEG, GIF, WebP, SVG)
- PDF / PPTX (конвертируется сервером)
- Стриминг (HLS, DASH)
- Папки (ZIP-архивы изображений)
- Crossfade переходы между файлами
- Автопереподключение при потере связи
- Watchdog для автоматического перезапуска
- Kiosk-режим (спрятать системные элементы)
- Обновление по воздуху (OTA)

## Требования

- Android 5.0+ (API 21)
- Подключение к интернету
- Доступ к MMRC серверу

## Сборка

```bash
# Сборка debug APK
./gradlew assembleDebug

# Сборка release APK
./gradlew assembleRelease
```

Результат: `app/build/outputs/apk/`

## Установка

### ADB (WiFi)

```bash
adb connect <IP-устройства>:5555
adb install app-release.apk
```

### На устройстве

Скопируйте APK на устройство и установите через файловый менеджер.

## Конфигурация

Плеер автоматически регистрируется на сервере после запуска. Настройки доступны в приложении:

- **Server URL** — адрес MMRC сервера
- **Device ID** — уникальный идентификатор устройства

## Автозапуск

Приложение поддерживает автозапуск при включении устройства. Включите опцию в настройках или используйте `BootReceiver`:

```xml
<receiver android:name=".BootReceiver" android:enabled="true">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

## Структура проекта

```
app/src/main/java/com/videocontrol/mediaplayer/
├── MainActivity.kt          # Основная логика плеера
├── SettingsActivity.kt      # Экран настроек
├── BootReceiver.kt          # Автозапуск
├── ConfigReceiver.kt        # Приём конфигурации
└── RemoteConfig.kt          # Удалённая конфигурация
```

## Зависимости

- ExoPlayer 2.19.1 — воспроизведение видео/аудио
- Socket.IO 2.1.0 — подключение к серверу
- Glide 4.16.0 — загрузка изображений
- AndroidSVG 1.4 — отрисовка SVG
