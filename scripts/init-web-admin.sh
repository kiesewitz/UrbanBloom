#!/bin/bash
# Init script for Next.js Admin Web

cd "$(dirname "$0")/../admin-web" || exit 1

npx create-next-app@latest . --typescript --eslint --src-dir --app --tailwind

