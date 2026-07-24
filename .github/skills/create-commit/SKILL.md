---
name: create-commit
description: >-
  Format git commit messages following CieloPass's Conventional Commits
  convention with mandatory scopes, structured body, and execute the git commit
  command. Use whenever the user asks to create a commit, write a commit
  message, generate a commit, or mentions committing changes.
---

# CieloPass Create Commit Skill

When creating a git commit for this repository, format a commit message that follows the Conventional Commits specification, verify git status/staged changes, and execute the git commit command.

## Workflow

1. **Check Staged Changes:** Verify staged files using `git status` or `git diff --cached`. If no changes are staged, prompt to stage relevant files first.
2. **Determine Scope & Type:** Select the appropriate type and scope for CieloPass.
3. **Draft Commit Message:** Format title, body, implementation/tests sections, and footers.
4. **Execute Commit:** Run `git commit -m "..."`

## Commit Message Format

Every commit message must follow this structure:

```
type(scope): subject

[body]

[optional organized sections]

[optional footer(s)]
```

## Format Rules

- **type** is mandatory and lowercase.
- **scope** is mandatory, lowercase, and must describe the affected area.
- **subject** is mandatory, lowercase, imperative, and must not end with a period.
- **subject** must be under 72 characters.
- **body** is mandatory and must explain what changed and why.
- Wrap all lines at 72 characters where possible.

## Allowed Types

| Type       | Description                                               |
| :--------- | :-------------------------------------------------------- |
| `feat`     | A new feature (MINOR version bump)                        |
| `fix`      | A bug fix (PATCH bump)                                    |
| `docs`     | Documentation only changes                                |
| `style`    | Formatting or code style changes (no logic changes)       |
| `refactor` | Code change that neither fixes a bug nor adds a feature   |
| `perf`     | Performance improvement                                   |
| `test`     | Adding or modifying unit / instrumented tests             |
| `build`    | Gradle dependencies or build configuration changes        |
| `ci`       | CI configuration files and scripts                        |
| `chore`    | Maintenance or minor updates                              |
| `revert`   | Reverts a previous commit                                 |

## CieloPass Scopes

Use the scope that matches the affected project area:

`cielo`, `database`, `datastore`, `network`, `navigation`, `ui`, `events`, `checkout`, `tickets`, `build`, `ci`, `docs`

If none of these fit, choose the smallest clear subsystem name and keep it lowercase.

## Subject Rules

**Do:**
- Use imperative mood: `add`, `fix`, `change`, `remove`
- Start with lowercase
- Keep it specific and under 72 characters

**Don't:**
- Use past tense (`added`, `fixed`)
- Capitalize the first word or end with a period

## Mandatory Body & Organized Sections

Explain what changed and why:

```
feat(cielo): add deeplink intent builder for payment

Implement the URI scheme builder for order://payment deep links targeting
Cielo Smart / LIO terminals.

Implementation:
- Add DeeplinkIntentBuilder in core/cielo
- Add Base64 response decoder for order://response callbacks

Tests:
- Add unit test coverage for Base64 JSON parser
```

## Executing the Commit

When executing the commit command:

### PowerShell (Windows)
```powershell
git commit -m "type(scope): subject" -m "body..."
```

### Bash / sh
```bash
git commit -m "type(scope): subject" -m "body..."
```

## Final Verification
Before running `git commit`, verify:
- Staged files match the intended changes.
- Type is valid and lowercase.
- Scope is present and matches CieloPass architecture.
- Subject is imperative, under 72 chars, no period.
- Body explains what and why.
