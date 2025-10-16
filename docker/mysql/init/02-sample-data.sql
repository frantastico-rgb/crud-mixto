-- 📊 Datos de ejemplo para DemoMixto
-- Este script inserta datos de prueba después de la inicialización

-- ============================================================================
-- ESPERAR A QUE SPRING BOOT CREE LAS TABLAS
-- ============================================================================
-- Este script se ejecutará, pero Spring Boot creará las tablas automáticamente
-- Los datos se insertarán solo si las tablas existen

-- ============================================================================
-- FUNCIÓN PARA INSERTAR DATOS SOLO SI LA TABLA EXISTE
-- ============================================================================
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS InsertSampleData()
BEGIN
    DECLARE table_exists INT DEFAULT 0;
    
    -- Verificar si la tabla empleado existe
    SELECT COUNT(*)
    INTO table_exists
    FROM information_schema.tables 
    WHERE table_schema = 'empresa' 
    AND table_name = 'empleado';
    
    -- Si la tabla existe y está vacía, insertar datos
    IF table_exists > 0 THEN
        -- Verificar si ya hay datos
        SET @row_count = (SELECT COUNT(*) FROM empleado);
        
        IF @row_count = 0 THEN
            -- Insertar empleados de ejemplo
            INSERT INTO empleado (nombre, cargo, salario, email) VALUES
            ('Juan Carlos Pérez', 'Desarrollador Senior', 75000.00, 'juan.perez@demomixto.com'),
            ('María Elena González', 'Product Manager', 85000.00, 'maria.gonzalez@demomixto.com'),
            ('Carlos Andrés Ruiz', 'DevOps Engineer', 70000.00, 'carlos.ruiz@demomixto.com'),
            ('Ana Sofía Martínez', 'UX/UI Designer', 60000.00, 'ana.martinez@demomixto.com'),
            ('Luis Fernando Castro', 'QA Tester', 55000.00, 'luis.castro@demomixto.com'),
            ('Diana Patricia López', 'Scrum Master', 80000.00, 'diana.lopez@demomixto.com'),
            ('Miguel Ángel Torres', 'Backend Developer', 65000.00, 'miguel.torres@demomixto.com'),
            ('Sandra Milena Vargas', 'Frontend Developer', 62000.00, 'sandra.vargas@demomixto.com'),
            ('Roberto José Herrera', 'Database Administrator', 68000.00, 'roberto.herrera@demomixto.com'),
            ('Laura Cristina Jiménez', 'Business Analyst', 58000.00, 'laura.jimenez@demomixto.com');
            
            SELECT 'Datos de ejemplo insertados en empleado' AS resultado;
        ELSE
            SELECT 'La tabla empleado ya contiene datos' AS resultado;
        END IF;
    ELSE
        SELECT 'La tabla empleado no existe aún - Spring Boot la creará automáticamente' AS resultado;
    END IF;
END$$

DELIMITER ;

-- Ejecutar el procedimiento (se intentará, pero puede fallar si la tabla no existe)
-- Spring Boot creará las tablas al iniciar, luego este script estará disponible
CALL InsertSampleData();

-- Limpiar el procedimiento temporal
DROP PROCEDURE IF EXISTS InsertSampleData;