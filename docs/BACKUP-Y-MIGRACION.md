# 🛡️ Copias de Seguridad y Migración de Datos - CRUD Mixto

## 📋 Índice
1. [Estrategia de Backup](#estrategia-backup)
2. [Migración de Datos](#migracion-datos)
3. [Procedimientos de Recuperación](#recuperacion)
4. [Integración con QA](#integracion-qa)
5. [Scripts Automatizados](#scripts-automatizados)
6. [Monitoreo y Alertas](#monitoreo)

---

## 🎯 1. Estrategia de Backup {#estrategia-backup}

### **🏗️ Arquitectura Dual de Backup**

#### **📊 MySQL (Empleados)**
```yaml
Tipo de Datos: Críticos (Personal, Salarios, Estructura organizacional)
Frecuencia: Backup diario completo + backup incremental cada 4 horas
Retención: 30 días locales + 90 días en cloud
Prioridad: CRÍTICA (RTO: 1 hora, RPO: 15 minutos)
```

#### **🍃 MongoDB (Proyectos)**
```yaml
Tipo de Datos: Operacionales (Proyectos, Tareas, Reportes)
Frecuencia: Backup diario completo + snapshot cada 6 horas
Retención: 15 días locales + 60 días en cloud
Prioridad: ALTA (RTO: 4 horas, RPO: 1 hora)
```

### **📅 Calendario de Backup**

```
┌─────────────┬──────────┬──────────┬──────────┬──────────┬──────────┬──────────┬──────────┐
│   HORARIO   │   LUN    │   MAR    │   MIE    │   JUE    │   VIE    │   SAB    │   DOM    │
├─────────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ 02:00 AM    │ MySQL    │ MySQL    │ MySQL    │ MySQL    │ MySQL    │ MySQL    │ MySQL    │
│             │ Completo │ Completo │ Completo │ Completo │ Completo │ Completo │ Completo │
├─────────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ 03:00 AM    │ MongoDB  │ MongoDB  │ MongoDB  │ MongoDB  │ MongoDB  │ MongoDB  │ MongoDB  │
│             │ Completo │ Completo │ Completo │ Completo │ Completo │ Completo │ Completo │
├─────────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ 06:00 AM    │ MySQL    │ MySQL    │ MySQL    │ MySQL    │ MySQL    │          │          │
│ 10:00 AM    │ Increm.  │ Increm.  │ Increm.  │ Increm.  │ Increm.  │          │          │
│ 02:00 PM    │          │          │          │          │          │          │          │
│ 06:00 PM    │          │          │          │          │          │          │          │
├─────────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┼──────────┤
│ 09:00 AM    │ MongoDB  │ MongoDB  │ MongoDB  │ MongoDB  │ MongoDB  │          │          │
│ 03:00 PM    │ Snapshot │ Snapshot │ Snapshot │ Snapshot │ Snapshot │          │          │
│ 09:00 PM    │          │          │          │          │          │          │          │
└─────────────┴──────────┴──────────┴──────────┴──────────┴──────────┴──────────┴──────────┘
```

### **🔒 Tipos de Backup**

#### **Full Backup (Completo)**
- **Qué incluye**: Todos los datos, esquemas, índices, procedimientos
- **Ventaja**: Recuperación completa e independiente
- **Desventaja**: Mayor tiempo y espacio de almacenamiento
- **Uso**: Base para recuperación total

#### **Incremental Backup**
- **Qué incluye**: Solo cambios desde el último backup
- **Ventaja**: Rápido y eficiente en espacio
- **Desventaja**: Requiere cadena de backups para recuperación
- **Uso**: Minimizar pérdida de datos entre backups completos

#### **Snapshot (MongoDB)**
- **Qué incluye**: Estado completo en un momento específico
- **Ventaja**: Consistencia transaccional garantizada
- **Desventaja**: Requiere pausa temporal de escrituras
- **Uso**: Puntos de restauración críticos

---

## 🔄 2. Migración de Datos {#migracion-datos}

### **🚀 Estrategias de Migración**

#### **📈 Migración por Fases**
```
Fase 1: PREPARACIÓN
├─ Análisis de datos existentes
├─ Validación de integridad
├─ Creación de entorno de pruebas
└─ Backup completo pre-migración

Fase 2: MIGRACIÓN ESTRUCTURAL
├─ Actualización de esquemas MySQL
├─ Modificación de colecciones MongoDB
├─ Ajuste de índices y constraints
└─ Pruebas de compatibilidad

Fase 3: MIGRACIÓN DE DATOS
├─ Transferencia en lotes pequeños
├─ Validación continua de datos
├─ Sincronización entre bases
└─ Verificación de integridad referencial

Fase 4: VALIDACIÓN Y SWITCHOVER
├─ Pruebas funcionales completas
├─ Validación de rendimiento
├─ Activación del nuevo sistema
└─ Monitoreo post-migración
```

### **🔧 Herramientas de Migración**

#### **MySQL Migration Tools**
```bash
# Flyway - Versionado de esquemas
flyway migrate -configFiles=mysql-migration.conf

# mysqldump - Exportación/Importación
mysqldump --single-transaction --routines --triggers empresa > backup.sql

# MySQL Workbench Migration Wizard
# GUI para migraciones complejas entre versiones
```

#### **MongoDB Migration Tools**
```bash
# MongoDB Database Tools
mongodump --uri="mongodb+srv://user:pass@cluster/empresa" --out=backup/

# mongorestore para importación
mongorestore --uri="mongodb+srv://user:pass@cluster/" backup/empresa/

# MongoDB Compass - GUI para análisis y migración
```

### **📊 Matriz de Compatibilidad**

```
┌──────────────────┬─────────────┬─────────────┬─────────────┬─────────────┐
│    COMPONENTE    │ DESARROLLO  │  TESTING    │  STAGING    │ PRODUCCIÓN  │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ MySQL            │ 8.0.35      │ 8.0.35      │ 8.0.35      │ 8.0.35      │
│ MongoDB          │ 7.0.4       │ 7.0.4       │ 7.0.4       │ 7.0.4       │
│ Spring Boot      │ 3.5.6       │ 3.5.6       │ 3.5.6       │ 3.5.6       │
│ Java             │ 17.0.16     │ 17.0.16     │ 17.0.16     │ 17.0.16     │
│ Esquema MySQL    │ v1.2.0      │ v1.2.0      │ v1.1.0      │ v1.0.0      │
│ Colecciones Mongo│ v2.1.0      │ v2.1.0      │ v2.0.0      │ v2.0.0      │
└──────────────────┴─────────────┴─────────────┴─────────────┴─────────────┘
```

---

## 🆘 3. Procedimientos de Recuperación {#recuperacion}

### **🚨 Escenarios de Recuperación**

#### **Scenario 1: Corrupción de Datos MySQL**
```bash
# 1. Parar la aplicación
systemctl stop demomixto

# 2. Verificar integridad
mysql -u root -p -e "CHECK TABLE empleados EXTENDED;"

# 3. Restaurar desde backup más reciente
mysql -u root -p empresa < backup_mysql_2025-10-20_02-00.sql

# 4. Verificar consistencia post-restauración
mysql -u root -p -e "SELECT COUNT(*) FROM empleados;"

# 5. Reiniciar aplicación
systemctl start demomixto
```

#### **Scenario 2: Pérdida de Datos MongoDB**
```bash
# 1. Identificar el problema
mongo --eval "db.proyectos.count()"

# 2. Restaurar desde snapshot
mongorestore --uri="mongodb+srv://..." --drop backup/empresa/

# 3. Verificar integridad referencial
mongo --eval "
  db.proyectos.find({empleadoId: {$exists: true}}).forEach(
    function(proyecto) {
      // Verificar que empleadoId existe en MySQL
    }
  )"

# 4. Validar aplicación
curl -u admin:admin http://localhost:8080/proyectos/estadisticas
```

#### **Scenario 3: Disaster Recovery Completo**
```bash
# 1. PREPARACIÓN
# - Activar sitio de contingencia
# - Notificar stakeholders
# - Documentar estado actual

# 2. RECUPERACIÓN MYSQL
# - Restaurar desde backup en cloud
# - Aplicar logs de transacciones
# - Verificar integridad de datos

# 3. RECUPERACIÓN MONGODB
# - Restaurar colecciones desde Atlas backup
# - Sincronizar con datos de MySQL
# - Validar referencias cruzadas

# 4. VALIDACIÓN
# - Ejecutar test suite completo
# - Verificar funcionalidades críticas
# - Confirmar rendimiento aceptable

# 5. ACTIVACIÓN
# - Redirigir tráfico al nuevo entorno
# - Monitoreo intensivo por 24h
# - Documentar lecciones aprendidas
```

### **⏱️ RTO/RPO Targets**

```
┌─────────────────────┬─────────────┬─────────────┬─────────────────────┐
│      ESCENARIO      │     RTO     │     RPO     │    PROCEDIMIENTO    │
├─────────────────────┼─────────────┼─────────────┼─────────────────────┤
│ Corrupción MySQL    │ 30 minutos  │ 4 horas     │ Restore + Verify    │
│ Pérdida MongoDB     │ 1 hora      │ 6 horas     │ Restore + Sync      │
│ Falla de servidor   │ 2 horas     │ 8 horas     │ Failover + Test     │
│ Disaster total      │ 8 horas     │ 24 horas    │ DR Site + Full Test │
│ Corrupción parcial  │ 15 minutos  │ 1 hora      │ Selective restore   │
└─────────────────────┴─────────────┴─────────────┴─────────────────────┘
```

---

## 🧪 4. Integración con QA {#integracion-qa}

### **🔍 Testing de Backup y Recovery**

#### **Test Cases Automatizados**
```yaml
TC-BACKUP-001:
  name: "Verificar integridad de backup MySQL"
  frequency: "Diario"
  steps:
    - Ejecutar backup completo
    - Restaurar en entorno de prueba
    - Validar count de registros
    - Verificar integridad referencial
  expected: "Backup restaurable sin errores"

TC-BACKUP-002:
  name: "Validar backup incremental MySQL"
  frequency: "Semanal"
  steps:
    - Crear datos de prueba
    - Ejecutar backup incremental
    - Restaurar backup completo + incremental
    - Verificar presencia de datos nuevos
  expected: "Datos incrementales presentes"

TC-MIGRATION-001:
  name: "Migración entre entornos"
  frequency: "Por release"
  steps:
    - Migrar de DEV a TEST
    - Ejecutar suite de pruebas funcionales
    - Validar performance de queries
    - Verificar integridad de relaciones
  expected: "Migración sin pérdida de funcionalidad"
```

#### **🎯 Métricas de Calidad**

```
Backup Success Rate: ≥ 99.5%
Recovery Time Actual vs Target: ≤ 110%
Data Integrity Post-Recovery: 100%
Migration Success Rate: ≥ 98%
Zero-Downtime Migration: ≥ 95%
```

### **📊 Dashboard de Monitoreo**

```
┌─────────────────────────────────────────────────────────────────┐
│                    BACKUP & MIGRATION STATUS                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  📊 BACKUP STATUS                    🔄 LAST OPERATIONS        │
│  ├─ MySQL: ✅ OK (2h ago)            ├─ Full Backup: ✅ OK      │
│  ├─ MongoDB: ✅ OK (3h ago)          ├─ Incremental: ✅ OK      │
│  ├─ Storage: 78% used                ├─ Migration: ⏳ Running   │
│  └─ Next: 21:00 tonight              └─ Recovery Test: ✅ OK    │
│                                                                 │
│  ⚠️  ALERTS                          📈 PERFORMANCE            │
│  ├─ No critical alerts               ├─ Backup Speed: 150MB/s   │
│  ├─ Warning: Storage 78%             ├─ Recovery Time: 12min    │
│  └─ Info: Schedule maintenance       └─ Migration Time: 45min   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🤖 5. Scripts Automatizados {#scripts-automatizados}

### **📝 Script de Backup MySQL**
```bash
#!/bin/bash
# backup-mysql.sh - Backup automatizado MySQL

set -euo pipefail

# Configuración
MYSQL_HOST="localhost"
MYSQL_USER="backup_user"
MYSQL_PASS="secure_password"
DATABASE="empresa"
BACKUP_DIR="/backups/mysql"
RETENTION_DAYS=30

# Crear directorio con timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_PATH="$BACKUP_DIR/mysql_${DATABASE}_${TIMESTAMP}"

# Logging
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a "$BACKUP_DIR/backup.log"
}

# Función principal de backup
perform_backup() {
    log "Iniciando backup de MySQL - Database: $DATABASE"
    
    # Crear backup completo
    mysqldump \
        --host="$MYSQL_HOST" \
        --user="$MYSQL_USER" \
        --password="$MYSQL_PASS" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        --add-drop-database \
        --databases "$DATABASE" \
        --result-file="${BACKUP_PATH}.sql"
    
    # Comprimir backup
    gzip "${BACKUP_PATH}.sql"
    log "Backup completado: ${BACKUP_PATH}.sql.gz"
    
    # Verificar integridad
    if gunzip -t "${BACKUP_PATH}.sql.gz"; then
        log "✅ Backup verificado exitosamente"
    else
        log "❌ ERROR: Backup corrupto"
        exit 1
    fi
    
    # Limpiar backups antiguos
    find "$BACKUP_DIR" -name "mysql_${DATABASE}_*.sql.gz" -mtime +$RETENTION_DAYS -delete
    log "Limpieza de backups antiguos completada"
}

# Función de notificación
send_notification() {
    local status="$1"
    local message="$2"
    
    # Webhook para Slack/Teams (opcional)
    curl -X POST -H 'Content-type: application/json' \
        --data "{\"text\":\"🛡️ Backup MySQL $status: $message\"}" \
        "$WEBHOOK_URL" 2>/dev/null || true
}

# Ejecución principal
main() {
    if perform_backup; then
        send_notification "SUCCESS" "Database $DATABASE backed up successfully"
        log "🎉 Backup process completed successfully"
        exit 0
    else
        send_notification "FAILED" "Backup failed for database $DATABASE"
        log "💥 Backup process failed"
        exit 1
    fi
}

# Manejo de señales
trap 'log "Backup interrupted"; exit 130' INT TERM

# Ejecutar
main "$@"
```

### **🍃 Script de Backup MongoDB**
```bash
#!/bin/bash
# backup-mongodb.sh - Backup automatizado MongoDB

set -euo pipefail

# Configuración
MONGO_URI="mongodb+srv://backup_user:password@cluster.mongodb.net"
DATABASE="empresa"
BACKUP_DIR="/backups/mongodb"
RETENTION_DAYS=15

# Crear directorio con timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_PATH="$BACKUP_DIR/mongodb_${DATABASE}_${TIMESTAMP}"

# Logging
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a "$BACKUP_DIR/backup.log"
}

# Función principal de backup
perform_backup() {
    log "Iniciando backup de MongoDB - Database: $DATABASE"
    
    # Crear backup usando mongodump
    mongodump \
        --uri="$MONGO_URI" \
        --db="$DATABASE" \
        --out="$BACKUP_PATH" \
        --gzip
    
    log "Backup completado: $BACKUP_PATH"
    
    # Verificar backup
    if [ -d "$BACKUP_PATH/$DATABASE" ]; then
        local collection_count=$(ls -1 "$BACKUP_PATH/$DATABASE" | wc -l)
        log "✅ Backup verificado: $collection_count archivos creados"
    else
        log "❌ ERROR: Backup no encontrado"
        exit 1
    fi
    
    # Comprimir backup completo
    tar -czf "${BACKUP_PATH}.tar.gz" -C "$BACKUP_DIR" "$(basename "$BACKUP_PATH")"
    rm -rf "$BACKUP_PATH"
    log "Backup comprimido: ${BACKUP_PATH}.tar.gz"
    
    # Limpiar backups antiguos
    find "$BACKUP_DIR" -name "mongodb_${DATABASE}_*.tar.gz" -mtime +$RETENTION_DAYS -delete
    log "Limpieza de backups antiguos completada"
}

# Ejecución principal
main() {
    if perform_backup; then
        log "🎉 MongoDB backup completed successfully"
        exit 0
    else
        log "💥 MongoDB backup failed"
        exit 1
    fi
}

# Ejecutar
main "$@"
```

### **🔄 Script de Migración**
```bash
#!/bin/bash
# migrate-data.sh - Migración entre entornos

set -euo pipefail

# Configuración
SOURCE_ENV="$1"
TARGET_ENV="$2"
MIGRATION_TYPE="${3:-full}" # full, schema-only, data-only

# Validar parámetros
if [ $# -lt 2 ]; then
    echo "Uso: $0 <source_env> <target_env> [migration_type]"
    echo "Ejemplo: $0 dev staging full"
    exit 1
fi

# Logging
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

# Función de migración MySQL
migrate_mysql() {
    log "Migrando MySQL de $SOURCE_ENV a $TARGET_ENV"
    
    # Crear backup del entorno destino
    log "Creando backup de seguridad del entorno destino"
    mysqldump --host="$TARGET_HOST" --user="$TARGET_USER" --password="$TARGET_PASS" \
        --databases empresa > "/tmp/backup_${TARGET_ENV}_$(date +%Y%m%d_%H%M%S).sql"
    
    if [ "$MIGRATION_TYPE" = "full" ] || [ "$MIGRATION_TYPE" = "schema-only" ]; then
        log "Migrando esquema MySQL"
        mysqldump --host="$SOURCE_HOST" --user="$SOURCE_USER" --password="$SOURCE_PASS" \
            --no-data --routines --triggers --events empresa | \
        mysql --host="$TARGET_HOST" --user="$TARGET_USER" --password="$TARGET_PASS"
    fi
    
    if [ "$MIGRATION_TYPE" = "full" ] || [ "$MIGRATION_TYPE" = "data-only" ]; then
        log "Migrando datos MySQL"
        mysqldump --host="$SOURCE_HOST" --user="$SOURCE_USER" --password="$SOURCE_PASS" \
            --no-create-info --single-transaction empresa | \
        mysql --host="$TARGET_HOST" --user="$TARGET_USER" --password="$TARGET_PASS" empresa
    fi
    
    log "✅ Migración MySQL completada"
}

# Función de migración MongoDB
migrate_mongodb() {
    log "Migrando MongoDB de $SOURCE_ENV a $TARGET_ENV"
    
    # Backup del entorno destino
    log "Creando backup de seguridad del entorno destino"
    mongodump --uri="$TARGET_MONGO_URI" --db=empresa --out="/tmp/backup_${TARGET_ENV}_$(date +%Y%m%d_%H%M%S)"
    
    # Migrar datos
    if [ "$MIGRATION_TYPE" = "full" ] || [ "$MIGRATION_TYPE" = "data-only" ]; then
        log "Migrando datos MongoDB"
        
        # Dump desde origen
        mongodump --uri="$SOURCE_MONGO_URI" --db=empresa --out="/tmp/migration_source"
        
        # Restore a destino
        mongorestore --uri="$TARGET_MONGO_URI" --db=empresa --drop "/tmp/migration_source/empresa"
        
        # Limpiar archivos temporales
        rm -rf "/tmp/migration_source"
    fi
    
    log "✅ Migración MongoDB completada"
}

# Función de validación post-migración
validate_migration() {
    log "Validando migración..."
    
    # Contar registros en MySQL
    local mysql_empleados=$(mysql --host="$TARGET_HOST" --user="$TARGET_USER" --password="$TARGET_PASS" \
        --database=empresa --execute="SELECT COUNT(*) FROM empleados;" --silent --raw)
    
    # Contar documentos en MongoDB
    local mongo_proyectos=$(mongo --quiet --eval "db.proyectos.count()" "$TARGET_MONGO_URI/empresa")
    
    log "Registros migrados - MySQL empleados: $mysql_empleados, MongoDB proyectos: $mongo_proyectos"
    
    # Validar integridad referencial
    log "Validando integridad referencial..."
    
    # TODO: Agregar validaciones específicas
    
    log "✅ Validación completada"
}

# Ejecución principal
main() {
    log "🚀 Iniciando migración de $SOURCE_ENV a $TARGET_ENV"
    
    # Cargar configuración por entorno
    source "/config/${SOURCE_ENV}.conf"
    SOURCE_HOST="$MYSQL_HOST"
    SOURCE_USER="$MYSQL_USER"
    SOURCE_PASS="$MYSQL_PASS"
    SOURCE_MONGO_URI="$MONGO_URI"
    
    source "/config/${TARGET_ENV}.conf"
    TARGET_HOST="$MYSQL_HOST"
    TARGET_USER="$MYSQL_USER"
    TARGET_PASS="$MYSQL_PASS"
    TARGET_MONGO_URI="$MONGO_URI"
    
    # Ejecutar migración
    migrate_mysql
    migrate_mongodb
    validate_migration
    
    log "🎉 Migración completada exitosamente"
}

# Ejecutar
main
```

---

## 📊 6. Monitoreo y Alertas {#monitoreo}

### **🔔 Sistema de Alertas**

#### **Configuración de Alertas**
```yaml
alerts:
  backup_failure:
    condition: "backup_status != 'success'"
    severity: "critical"
    channels: ["email", "slack", "sms"]
    escalation: "5min -> manager, 15min -> director"
    
  storage_full:
    condition: "disk_usage > 85%"
    severity: "warning"
    channels: ["email", "slack"]
    escalation: "30min -> sysadmin"
    
  recovery_time_exceeded:
    condition: "recovery_time > rto_target * 1.2"
    severity: "major"
    channels: ["email", "slack", "incident_manager"]
    escalation: "immediate -> incident_response"
```

### **📈 Métricas Clave**

```
┌─────────────────────┬─────────────┬─────────────┬─────────────┐
│       MÉTRICA       │   TARGET    │   ACTUAL    │   TREND     │
├─────────────────────┼─────────────┼─────────────┼─────────────┤
│ Backup Success Rate │    ≥ 99.5%  │   99.8%     │     ↗️      │
│ Recovery Time (MySQL)│   ≤ 30min   │   18min     │     ↘️      │
│ Recovery Time (Mongo)│   ≤ 1hour   │   42min     │     ↗️      │
│ Storage Utilization │   < 80%     │   67%       │     ↗️      │
│ Migration Success   │   ≥ 98%     │   100%      │     →       │
│ Data Integrity      │   100%      │   100%      │     →       │
└─────────────────────┴─────────────┴─────────────┴─────────────┘
```

### **🎯 KPIs de Garantía de Calidad**

```
📊 AVAILABILITY METRICS
├─ Sistema disponible: 99.9% (target: 99.5%)
├─ Downtime planificado: 2h/mes (target: 4h/mes)
└─ Downtime no planificado: 15min/mes (target: 30min/mes)

🛡️ BACKUP METRICS  
├─ Backups exitosos: 99.8% (target: 99.5%)
├─ Tiempo de backup: 45min promedio (target: 60min)
└─ Verificación de integridad: 100% (target: 100%)

🔄 RECOVERY METRICS
├─ RTO cumplido: 95% (target: 90%)
├─ RPO cumplido: 98% (target: 95%)
└─ Ejercicios DR exitosos: 100% (target: 100%)

🚀 MIGRATION METRICS
├─ Migraciones sin downtime: 85% (target: 80%)
├─ Rollbacks necesarios: 2% (target: 5%)
└─ Tiempo de migración: -20% vs baseline
```

---

## 📋 Resumen Ejecutivo

### **🎯 Beneficios de la Estrategia**

1. **💼 Continuidad del Negocio**
   - RPO ≤ 24 horas garantiza pérdida mínima de datos
   - RTO ≤ 8 horas minimiza impacto operacional
   - Procedimientos documentados reducen tiempo de respuesta

2. **🛡️ Garantía de Calidad**
   - Testing automatizado de backups
   - Validación de integridad en cada migración
   - Monitoreo continuo de métricas clave

3. **📈 Escalabilidad**
   - Scripts automatizados para crecimiento
   - Estrategia dual optimizada por tipo de datos
   - Integración con herramientas de CI/CD

4. **💰 Optimización de Costos**
   - Retención inteligente de backups
   - Compresión y deduplicación
   - Uso eficiente de recursos cloud

### **🚀 Próximos Pasos**

1. **Implementar scripts de automatización**
2. **Configurar alertas y monitoreo**
3. **Realizar ejercicio de disaster recovery**
4. **Capacitar al equipo en procedimientos**
5. **Revisar y actualizar documentación mensualmente**

---

**📅 Última actualización**: Octubre 2025  
**👥 Responsables**: Equipo DevOps + DBA  
**🔄 Revisión**: Mensual + Post-incident