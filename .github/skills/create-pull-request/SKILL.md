---
name: create-pull-request
description: >-
  Create, draft, and manage GitHub pull requests for CieloPass following the
  project's PR workflow — Conventional Commits titles, template-driven bodies,
  label assignment, and creating the pull request via GitHub MCP or gh CLI.
  Use whenever the user asks to create a PR, open a pull request, or draft a PR.
---

# CieloPass Create Pull Request Skill

When creating or managing a pull request for this repository, follow the workflow below to create a
reviewer-ready PR with a Conventional Commits title, a structured body from the project template,
appropriate labels, and execute the PR creation.

## Workflow

1. **Identify Context:** Determine the current branch and target branch (`main`).
2. **Generate Title:** Format as `type(scope): subject` using the Conventional Commits rules.
3. **Read Template:** Read `templates/pr-template.md` as the structural source.
4. **Build Body:** Fill in the template with concrete technical details and file paths.
5. **Assign Labels:** Select 3–5 labels based on type, priority, and context.
6. **Create PR:** Create the pull request using GitHub MCP tool (`create_pull_request`), targeting
   `main`. Fall back to `gh pr create` if MCP tool is unavailable.

## Title Format

PR titles follow the Conventional Commits format:

```
type(scope): subject
```

Rules:

- **type:** `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`,
  `revert`
- **scope:** `cielo`, `database`, `datastore`, `network`, `navigation`, `ui`, `events`, `checkout`,
  `tickets`, `build`, `ci`, `docs`
- **subject:** lowercase, imperative, no period at end, max 72 chars

## Body Generation

Use `templates/pr-template.md` as the structure. Fill in every section with real data:

- Choose exactly one task-type section (**Feature Additions**, **Technical Improvements**, or **Bug
  Fix Details**).
- Remove the unused task-type sections.
- List modified/created files using exact repo paths (`app/src/main/java/com/cielo/cielopass/...`).

## Label Assignment

Assign 3 to 5 labels:

**Type (1 required):** `type: feat`, `type: fix`, `type: docs`, `type: style`, `type: refactor`,
`type: perf`, `type: test`, `type: build`, `type: ci`, `type: chore`

**Priority (1 required):** `priority: critical`, `priority: high`, `priority: medium`,
`priority: low`

**Context (1–2):**

- Domain: `domain: cielo`, `domain: events`, `domain: checkout`, `domain: tickets`
- Infrastructure: `infra: database`, `infra: network`, `infra: build`, `infra: ci`

## Executing PR Creation

### Primary Method: GitHub MCP (`create_pull_request`)

Call `create_pull_request` with:

- `owner`: `gomes-eric`
- `repo`: `cielo-pass`
- `title`: `type(scope): subject`
- `head`: `<current_branch>`
- `base`: `main`
- `body`: `<generated_body_content>`

### Fallback Method: GitHub CLI (`gh`)

If GitHub MCP is unavailable:

```bash
gh pr create --base main --head <current_branch> --title "type(scope): subject" --body "<generated_body_content>"
```

## Quality Checks

Before creating the PR, verify:

- Current branch has pushed commits to origin.
- Base branch is `main`.
- Title matches `type(scope): subject`.
- PR body is written in English and removes unused template sections.
