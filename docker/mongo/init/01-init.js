// 🍃 Script de inicialización MongoDB para DemoMixto
// Este script se ejecuta automáticamente cuando se crea el contenedor MongoDB

// ============================================================================
// CONFIGURACIÓN INICIAL
// ============================================================================
print('🍃 Iniciando configuración de MongoDB para DemoMixto...');

// Conectar a la base de datos admin
db = db.getSiblingDB('admin');

// ============================================================================
// CREACIÓN DE USUARIO PARA LA APLICACIÓN
// ============================================================================
print('👤 Creando usuario para la aplicación...');

// Crear usuario específico para DemoMixto
db.createUser({
  user: 'demomixto',
  pwd: 'DemoMixto2025!',
  roles: [
    {
      role: 'readWrite',
      db: 'empresa'
    },
    {
      role: 'dbAdmin',
      db: 'empresa'
    }
  ]
});

print('✅ Usuario demomixto creado correctamente');

// ============================================================================
// CONFIGURACIÓN DE LA BASE DE DATOS EMPRESA
// ============================================================================
// Cambiar a la base de datos empresa
db = db.getSiblingDB('empresa');

print('📊 Configurando base de datos empresa...');

// ============================================================================
// CREACIÓN DE COLECCIONES
// ============================================================================
// Crear colección de proyectos con validación de esquema
db.createCollection('proyectos', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['nombre', 'descripcion', 'empleadoId', 'fechaInicio', 'completado'],
      properties: {
        nombre: {
          bsonType: 'string',
          description: 'Nombre del proyecto - requerido'
        },
        descripcion: {
          bsonType: 'string',
          description: 'Descripción del proyecto - requerida'
        },
        empleadoId: {
          bsonType: 'long',
          description: 'ID del empleado responsable - requerido'
        },
        fechaInicio: {
          bsonType: 'date',
          description: 'Fecha de inicio del proyecto - requerida'
        },
        completado: {
          bsonType: 'bool',
          description: 'Estado de completado del proyecto - requerido'
        },
        tareas: {
          bsonType: 'array',
          description: 'Array de tareas del proyecto',
          items: {
            bsonType: 'object',
            required: ['titulo', 'estado'],
            properties: {
              titulo: {
                bsonType: 'string',
                description: 'Título de la tarea'
              },
              estado: {
                bsonType: 'string',
                enum: ['pendiente', 'en-progreso', 'completo'],
                description: 'Estado de la tarea'
              }
            }
          }
        }
      }
    }
  },
  validationLevel: 'moderate',
  validationAction: 'warn'
});

print('✅ Colección proyectos creada con validación de esquema');

// ============================================================================
// CREACIÓN DE ÍNDICES
// ============================================================================
print('🔍 Creando índices para optimización...');

// Índice en empleadoId para búsquedas rápidas
db.proyectos.createIndex({ 'empleadoId': 1 }, { name: 'idx_empleadoId' });

// Índice en nombre para búsquedas de texto
db.proyectos.createIndex({ 'nombre': 1 }, { name: 'idx_nombre' });

// Índice en completado para filtros de estado
db.proyectos.createIndex({ 'completado': 1 }, { name: 'idx_completado' });

// Índice en fechaInicio para ordenamiento temporal
db.proyectos.createIndex({ 'fechaInicio': -1 }, { name: 'idx_fechaInicio' });

// Índice compuesto para consultas complejas
db.proyectos.createIndex(
  { 'empleadoId': 1, 'completado': 1, 'fechaInicio': -1 }, 
  { name: 'idx_empleado_estado_fecha' }
);

// Índice de texto para búsqueda full-text
db.proyectos.createIndex(
  { 'nombre': 'text', 'descripcion': 'text' },
  { name: 'idx_fulltext_search' }
);

print('✅ Índices creados correctamente');

// ============================================================================
// DATOS DE EJEMPLO
// ============================================================================
print('📋 Insertando datos de ejemplo...');

// Datos de proyectos de ejemplo
const proyectosEjemplo = [
  {
    nombre: 'Sistema de Gestión Web',
    descripcion: 'Desarrollo de aplicación web para gestión empresarial con Spring Boot y React',
    empleadoId: NumberLong(1),
    fechaInicio: new Date('2025-01-15'),
    completado: false,
    tareas: [
      { titulo: 'Diseño de arquitectura', estado: 'completo' },
      { titulo: 'Desarrollo backend API', estado: 'en-progreso' },
      { titulo: 'Desarrollo frontend', estado: 'pendiente' },
      { titulo: 'Testing integrado', estado: 'pendiente' }
    ]
  },
  {
    nombre: 'API REST para Móviles',
    descripcion: 'Desarrollo de API REST escalable para aplicaciones móviles',
    empleadoId: NumberLong(2),
    fechaInicio: new Date('2025-01-10'),
    completado: false,
    tareas: [
      { titulo: 'Documentación de endpoints', estado: 'completo' },
      { titulo: 'Implementación CRUD', estado: 'completo' },
      { titulo: 'Autenticación JWT', estado: 'en-progreso' },
      { titulo: 'Rate limiting', estado: 'pendiente' }
    ]
  },
  {
    nombre: 'Dashboard Analítico',
    descripcion: 'Dashboard con métricas y reportes en tiempo real usando MongoDB y D3.js',
    empleadoId: NumberLong(3),
    fechaInicio: new Date('2024-12-01'),
    completado: true,
    tareas: [
      { titulo: 'Diseño de mockups', estado: 'completo' },
      { titulo: 'Integración con MongoDB', estado: 'completo' },
      { titulo: 'Visualizaciones D3.js', estado: 'completo' },
      { titulo: 'Deploy en producción', estado: 'completo' }
    ]
  },
  {
    nombre: 'Migración a Microservicios',
    descripcion: 'Refactorización de monolito a arquitectura de microservicios con Docker',
    empleadoId: NumberLong(4),
    fechaInicio: new Date('2025-02-01'),
    completado: false,
    tareas: [
      { titulo: 'Análisis de dependencias', estado: 'pendiente' },
      { titulo: 'Separación de servicios', estado: 'pendiente' },
      { titulo: 'Configuración Docker', estado: 'pendiente' },
      { titulo: 'Orquestación Kubernetes', estado: 'pendiente' }
    ]
  },
  {
    nombre: 'Automatización CI/CD',
    descripcion: 'Pipeline completo de integración y despliegue continuo con GitHub Actions',
    empleadoId: NumberLong(5),
    fechaInicio: new Date('2025-01-20'),
    completado: false,
    tareas: [
      { titulo: 'Configuración GitHub Actions', estado: 'en-progreso' },
      { titulo: 'Tests automatizados', estado: 'pendiente' },
      { titulo: 'Deploy staging', estado: 'pendiente' },
      { titulo: 'Deploy producción', estado: 'pendiente' }
    ]
  }
];

// Insertar proyectos de ejemplo
db.proyectos.insertMany(proyectosEjemplo);

print('✅ Datos de ejemplo insertados correctamente');

// ============================================================================
// VERIFICACIÓN DE LA CONFIGURACIÓN
// ============================================================================
print('🔍 Verificando configuración...');

print('📊 Estadísticas de la base de datos:');
print('   - Colecciones: ' + db.getCollectionNames().length);
print('   - Proyectos: ' + db.proyectos.countDocuments());

print('📋 Índices creados:');
db.proyectos.getIndexes().forEach(index => {
  print('   - ' + index.name);
});

// ============================================================================
// CONFIGURACIÓN DE RENDIMIENTO
// ============================================================================
print('⚡ Aplicando configuraciones de rendimiento...');

// Configurar profiling para consultas lentas (solo en desarrollo)
db.setProfilingLevel(1, { slowms: 100 });

print('✅ Configuración de MongoDB completada exitosamente');
print('🚀 DemoMixto MongoDB está listo para usar');

// ============================================================================
// FINALIZACIÓN
// ============================================================================
print('📋 Resumen de configuración:');
print('   🗄️ Base de datos: empresa');
print('   👤 Usuario: demomixto');
print('   📊 Colección: proyectos');
print('   🔍 Índices: ' + db.proyectos.getIndexes().length);
print('   📋 Documentos: ' + db.proyectos.countDocuments());
print('   ✅ Estado: Configuración completada');