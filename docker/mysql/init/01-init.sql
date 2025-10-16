-- 🗄️ Script de inicialización MySQL para DemoMixto
-- Este script se ejecuta automáticamente cuando se crea el contenedor MySQL

-- ============================================================================
-- CONFIGURACIÓN INICIAL
-- ============================================================================
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER SET utf8mb4;

-- ============================================================================
-- CREACIÓN DE USUARIO Y PERMISOS
-- ============================================================================
-- Crear usuario específico para la aplicación
CREATE USER IF NOT EXISTS 'demomixto'@'%' IDENTIFIED BY 'DemoMixto2025!';

-- Otorgar permisos completos sobre la base de datos empresa
GRANT ALL PRIVILEGES ON empresa.* TO 'demomixto'@'%';

-- Permitir conexiones desde cualquier host (para contenedores)
GRANT ALL PRIVILEGES ON empresa.* TO 'demomixto'@'localhost';

-- Aplicar cambios de permisos
FLUSH PRIVILEGES;

-- ============================================================================
-- CONFIGURACIÓN DE LA BASE DE DATOS
-- ============================================================================
-- Usar la base de datos empresa (ya creada por MYSQL_DATABASE)
USE empresa;

-- ============================================================================
-- DATOS DE PRUEBA PARA EMPLEADOS
-- ============================================================================
-- Insertar empleados de ejemplo (solo si la tabla está vacía)
-- Spring Boot creará automáticamente las tablas con @Entity

-- Nota: Los siguientes INSERTs se ejecutarán después de que Spring Boot
-- cree las tablas automáticamente. Para datos iniciales, ver el script
-- 02-sample-data.sql

-- ============================================================================
-- CONFIGURACIÓN DE TIMEZONE
-- ============================================================================
-- Configurar zona horaria para Colombia
SET time_zone = '-05:00';

-- ============================================================================
-- OPTIMIZACIONES DE RENDIMIENTO
-- ============================================================================
-- Configuraciones específicas para el contenedor
SET GLOBAL innodb_buffer_pool_size = 134217728;  -- 128MB
SET GLOBAL max_connections = 50;
SET GLOBAL innodb_log_file_size = 50331648;      -- 48MB

-- ============================================================================
-- LOGGING Y MONITOREO
-- ============================================================================
-- Habilitar slow query log para monitoreo
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- ============================================================================
-- FINALIZACIÓN
-- ============================================================================
-- Mostrar información de la configuración
SELECT 'MySQL inicializado correctamente para DemoMixto' AS mensaje;
SELECT USER() AS usuario_actual;
SELECT DATABASE() AS base_datos_actual;
SELECT @@character_set_database AS charset_db;
SELECT @@collation_database AS collation_db;
SELECT @@time_zone AS zona_horaria;