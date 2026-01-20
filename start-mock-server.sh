#!/bin/bash
# Mock API Server Startup Script for Linux/Mac
# Requires: npm install -g @stoplight/prism-cli

echo "========================================"
echo "  Mock API Server - Schulbibliothek"
echo "========================================"
echo ""
echo "Starting Prism Mock Server..."
echo "OpenAPI Spec: docs/api/user.yaml"
echo "Port: 4010"
echo "CORS: Enabled"
echo ""
echo "Server will be available at:"
echo "  http://localhost:4010"
echo ""
echo "Press Ctrl+C to stop the server"
echo "========================================"
echo ""

prism mock docs/api/user.yaml --port 4010 --cors
