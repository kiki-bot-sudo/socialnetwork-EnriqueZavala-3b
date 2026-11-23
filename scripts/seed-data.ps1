<#
.SYNOPSIS
    Carga datos de prueba en el backend de Social Network usando la API REST.

.DESCRIPTION
    Crea usuarios, inicia sesión con cada uno, publica fotografías, agrega
    comentarios y likes. Se usa la API (no SQL directo) para que las
    contraseñas se guarden cifradas con BCrypt y el login funcione.

    Se puede ejecutar varias veces: si un usuario ya existe solo inicia sesión,
    las fotos que ya existen (mismo título y dueño) se omiten y los likes
    repetidos se ignoran. Solo las fotos nuevas reciben comentarios y likes.

    Requisito: el backend debe estar corriendo (.\gradlew bootRun).

.PARAMETER BaseUrl
    URL del backend. Por defecto http://localhost:8090

.EXAMPLE
    .\scripts\seed-data.ps1

.EXAMPLE
    .\scripts\seed-data.ps1 -BaseUrl http://localhost:9000
#>
param(
    [string]$BaseUrl = 'http://localhost:8090'
)

$ErrorActionPreference = 'Stop'

# Usuarios de prueba SOLO para desarrollo local. No uses estas contraseñas en otro lado.
$users = @(
    @{ username = 'ana_foto';     email = 'ana@example.com';    password = 'Prueba123!'; name = 'Ana López';      biography = 'Fotografía de paisajes y montaña'; profilePicture = 'https://picsum.photos/seed/ana/200' },
    @{ username = 'carlos_lente'; email = 'carlos@example.com'; password = 'Prueba123!'; name = 'Carlos Méndez';  biography = 'Retratos y fotografía urbana';     profilePicture = 'https://picsum.photos/seed/carlos/200' },
    @{ username = 'sofia_click';  email = 'sofia@example.com';  password = 'Prueba123!'; name = 'Sofía Ramírez';  biography = 'Amante de los atardeceres';        profilePicture = 'https://picsum.photos/seed/sofia/200' }
)

# Fotos por usuario (la clave es el email del dueño). 12 fotos en 8 categorías.
# Categorías válidas: Paisaje, Retrato, Urbano, Naturaleza, Animales, Arquitectura, Comida, Atardecer
$photos = @{
    'ana@example.com' = @(
        @{ title = 'Amanecer en la sierra'; description = 'Primeras luces sobre las montañas'; imageUrl = 'https://picsum.photos/seed/sierra/800/600';   category = 'Paisaje' },
        @{ title = 'Lago en calma';         description = 'Reflejos al mediodía';              imageUrl = 'https://picsum.photos/seed/lago/800/600';     category = 'Naturaleza' },
        @{ title = 'Bosque de niebla';      description = 'Caminata por la mañana';            imageUrl = 'https://picsum.photos/seed/bosque/800/600';   category = 'Naturaleza' },
        @{ title = 'Valle desde el mirador'; description = 'Vista panorámica';                 imageUrl = 'https://picsum.photos/seed/valle/800/600';    category = 'Paisaje' }
    )
    'carlos@example.com' = @(
        @{ title = 'Calles del centro';     description = 'Fotografía urbana en blanco y negro'; imageUrl = 'https://picsum.photos/seed/centro/800/600'; category = 'Urbano' },
        @{ title = 'Retrato de estudio';    description = 'Luz suave de ventana';              imageUrl = 'https://picsum.photos/seed/retrato/800/600';  category = 'Retrato' },
        @{ title = 'Catedral de noche';     description = 'Arquitectura colonial iluminada';   imageUrl = 'https://picsum.photos/seed/catedral/800/600'; category = 'Arquitectura' },
        @{ title = 'Tacos al pastor';       description = 'Puesto de la esquina';              imageUrl = 'https://picsum.photos/seed/tacos/800/600';    category = 'Comida' }
    )
    'sofia@example.com' = @(
        @{ title = 'Atardecer en la playa'; description = 'Foto tomada en Puerto Vallarta';    imageUrl = 'https://picsum.photos/seed/playa/800/600';    category = 'Atardecer' },
        @{ title = 'Cielo naranja';         description = 'Nubes al final del día';            imageUrl = 'https://picsum.photos/seed/cielo/800/600';    category = 'Atardecer' },
        @{ title = 'Mi gato Michi';         description = 'Durmiendo al sol';                  imageUrl = 'https://picsum.photos/seed/gato/800/600';     category = 'Animales' },
        @{ title = 'Puente moderno';        description = 'Líneas y sombras';                  imageUrl = 'https://picsum.photos/seed/puente/800/600';   category = 'Arquitectura' }
    )
}

$comments = @(
    '¡Qué buena luz!',
    'Me encanta la composición',
    'Los colores están increíbles',
    '¿Con qué cámara la tomaste?'
)

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        [object]$Body,
        [string]$Token
    )
    $headers = @{}
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }

    # Enviar el JSON como bytes UTF-8 para que los acentos lleguen bien (PowerShell 5.1).
    $json = $Body | ConvertTo-Json -Depth 5
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)

    Invoke-RestMethod -Method $Method -Uri "$BaseUrl$Path" -Headers $headers `
        -ContentType 'application/json; charset=utf-8' -Body $bytes
}

function Get-StatusCode($errorRecord) {
    $response = $errorRecord.Exception.Response
    if ($null -eq $response) { return $null }
    return [int]$response.StatusCode
}

# 0. Comprobar que el backend responde
try {
    Invoke-WebRequest -Uri "$BaseUrl/v3/api-docs" -UseBasicParsing -TimeoutSec 5 | Out-Null
} catch {
    Write-Host "No se pudo conectar con $BaseUrl. ¿Está corriendo el backend (.\gradlew bootRun)?" -ForegroundColor Red
    exit 1
}

# 1. Registrar usuarios e iniciar sesión
$tokens = @{}
foreach ($user in $users) {
    try {
        Invoke-Api -Method Post -Path '/api/users' -Body $user | Out-Null
        Write-Host "Usuario creado: $($user.username)" -ForegroundColor Green
    } catch {
        if ((Get-StatusCode $_) -eq 409) {
            Write-Host "El usuario $($user.username) ya existía, se reutiliza" -ForegroundColor Yellow
        } else {
            throw
        }
    }

    $login = Invoke-Api -Method Post -Path '/auth/login' -Body @{ email = $user.email; password = $user.password }
    $tokens[$user.email] = $login.token
}

# 2. Publicar fotos con el token de cada dueño (sin duplicar las que ya existen)
$existing = @(Invoke-RestMethod -Uri "$BaseUrl/api/photographs" -Headers @{ Authorization = "Bearer $($tokens[$users[0].email])" })
$createdPhotos = @()
foreach ($email in $photos.Keys) {
    foreach ($photo in $photos[$email]) {
        $already = $existing | Where-Object { $_.title -eq $photo.title -and $_.user.email -eq $email }
        if ($already) {
            Write-Host "La foto '$($photo.title)' ya existía, se omite" -ForegroundColor Yellow
            continue
        }
        $saved = Invoke-Api -Method Post -Path '/api/photographs' -Body $photo -Token $tokens[$email]
        $createdPhotos += [pscustomobject]@{ Id = $saved.id; Owner = $email; Title = $photo.title }
        Write-Host "Foto publicada: '$($photo.title)' (ID $($saved.id))" -ForegroundColor Green
    }
}

# 3. Cada usuario comenta y da like a las fotos de los demás
$commentCount = 0
$likeCount = 0
foreach ($user in $users) {
    $token = $tokens[$user.email]
    foreach ($photo in $createdPhotos | Where-Object { $_.Owner -ne $user.email }) {
        $text = $comments[$commentCount % $comments.Count]
        Invoke-Api -Method Post -Path '/api/comments' -Token $token `
            -Body @{ content = $text; photograph = @{ id = $photo.Id } } | Out-Null
        $commentCount++

        try {
            Invoke-Api -Method Post -Path '/api/likes' -Token $token `
                -Body @{ photograph = @{ id = $photo.Id } } | Out-Null
            $likeCount++
        } catch {
            if ((Get-StatusCode $_) -ne 409) { throw }
        }
    }
}

Write-Host ''
Write-Host 'Datos de prueba cargados:' -ForegroundColor Cyan
Write-Host "  Usuarios:    $($users.Count)"
Write-Host "  Fotos:       $($createdPhotos.Count)"
Write-Host "  Comentarios: $commentCount"
Write-Host "  Likes:       $likeCount"
Write-Host ''
Write-Host 'Puedes iniciar sesión en Swagger con cualquiera de estos correos:' -ForegroundColor Cyan
$users | ForEach-Object { Write-Host "  $($_.email)" }
Write-Host '(la contraseña de prueba está al inicio de scripts\seed-data.ps1)'
