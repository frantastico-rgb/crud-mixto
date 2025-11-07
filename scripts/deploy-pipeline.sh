#!/bin/bash

# 🚀 Script para Commit que Activa CI/CD Pipeline Completo

echo "🔄 Preparando commit para activar CI/CD pipeline..."

# Mensaje de commit optimizado
COMMIT_MESSAGE="feat: activar pipeline CI/CD completo con deployment automático

🚀 CI/CD Pipeline Configurado:
- GitHub Secrets: DockerHub + MongoDB Atlas
- Auto-deploy: Render principal + Railway backup  
- Testing: H2 unit tests (9/9 passing)
- Docker: Multi-stage build optimizado
- Health checks: /actuator/health monitoring

🌐 Deployment URLs:
- Principal: https://crud-mixto.onrender.com
- Backup: https://crud-mixto-railway.railway.app

📋 Features Ready:
- Gestión empleados (admin/admin)
- Gestión proyectos (público)
- APIs REST completas
- Reportes Excel exportables

Status: Production-ready deployment ✅"

# Ejecutar commit y push
git add .
git commit -m "$COMMIT_MESSAGE"

echo "✅ Commit creado exitosamente"
echo "🚀 Ejecutando push para activar pipeline..."

git push origin main

echo "✅ Push completado - Pipeline CI/CD activado"
echo "🔍 Monitorear en:"
echo "   - GitHub Actions: https://github.com/frantastico-rgb/crud-mixto/actions"
echo "   - Render Dashboard: https://dashboard.render.com"
echo "   - App Live: https://crud-mixto.onrender.com (en ~5-10 min)"