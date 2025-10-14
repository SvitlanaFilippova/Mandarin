#!/bin/bash
set -e

INFO_PLIST="$PROJECT_DIR/Info.plist"
PRIVATE_PLIST="$PROJECT_DIR/Info.private.plist"
MERGED_PLIST="$BUILT_PRODUCTS_DIR/$INFOPLIST_PATH"

if [ -f "$PRIVATE_PLIST" ]; then
    echo "🔐 Объединяю Info.private.plist с Info.plist..."
    /usr/libexec/PlistBuddy -x -c "Merge $PRIVATE_PLIST" "$MERGED_PLIST"
else
    echo "⚠️  Info.private.plist не найден. Использую только Info.plist"
fi