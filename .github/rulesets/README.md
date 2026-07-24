# GitHub Rulesets for CieloPass

This directory contains the repository rulesets aligned with CieloPass's branch strategy and CI
pipeline.

## Structure

```text
.github/rulesets/
├── branch-rulesets/
│   └── 01-main-protection.json
├── push-rulesets/
│   └── 01-sensitive-files-protection.json
├── tag-rulesets/
│   └── 01-release-tags-protection.json
└── README.md
```

## Branch Strategy Alignment

- `main` is the primary protected branch.
- `feature/*`, `fix/*`, `chore/*`, and `refactor/*` are short-lived topic branches.
- All changes merge back into `main` through pull requests.

## Configured Rulesets

### Main Branch Protection

- File: `branch-rulesets/01-main-protection.json`
- Target: `refs/heads/main`
- Required status checks:
    - `PR Check`
    - `Ktlint Check`
    - `Android Unit Tests & Build`
- Additional protections:
    - block force-push
    - require linear history
    - block deletion

### Sensitive Files Protection

- File: `push-rulesets/01-sensitive-files-protection.json`
- Scope: entire repository
- Blocks secrets and Android signing keys (`.keystore`, `.jks`, `.pem`, `.key`, `.env*`).
- Enforces max file size (100 MB) and max file path length (255 chars).

### Release Tag Protection

- File: `tag-rulesets/01-release-tags-protection.json`
- Target: `refs/tags/v*`
- Protects creation, deletion, and updates of release tags.
