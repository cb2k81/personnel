#!/bin/bash

echo "🔎 Repariere Getter/Setter bei DomainEntity-Subklassen..."

FILES=$(grep -rl "@Entity" src/main/java \
        | xargs grep -l "extends DomainEntity")

for FILE in $FILES; do
  echo "🛠 Repariere $FILE"

  # Lombok Getter/Setter Import hinzufügen, falls nicht vorhanden
  grep -q "import lombok.Getter;" "$FILE" || \
    sed -i '1,/^import/ s/^import/import lombok.Getter;\nimport lombok.Setter;\n&/' "$FILE"

  # Annotation hinzufügen, falls nicht vorhanden
  grep -q "@Getter" "$FILE" || \
    sed -i 's/@Entity/@Entity\n@Getter\n@Setter/' "$FILE"

done

echo "✅ Getter/Setter wiederhergestellt."