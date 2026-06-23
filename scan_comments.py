import re
from pathlib import Path

root = Path('.').resolve()
exclude_dirs = {'target', '.git', '.idea'}
pattern = re.compile(r'(<!--|-->|/\*|\*/|^\s*//|^\s*#|^\s*REM\b|^\s*@REM\b)', re.MULTILINE)

for path in sorted(root.rglob('*')):
    if path.is_dir():
        continue
    if any(part in exclude_dirs for part in path.parts):
        continue
    if path.suffix.lower() in {'.java','.html','.xml','.properties','.cmd','.bat','.sh','.bash','.zsh','.ps1','.md','.yaml','.yml','.gitignore','.txt','.ini'} or path.name in {'mvnw','pom.xml'}:
        try:
            text = path.read_text(encoding='utf-8')
        except Exception:
            try:
                text = path.read_text(encoding='latin-1')
            except Exception:
                continue
        matches = list(pattern.finditer(text))
        if matches:
            print(path.relative_to(root))
            for m in matches[:20]:
                line = text.count('\n', 0, m.start()) + 1
                print(f'  line {line}: {repr(text[m.start():m.end()])}')
            if len(matches) > 20:
                print(f'  ... {len(matches)-20} more matches')
