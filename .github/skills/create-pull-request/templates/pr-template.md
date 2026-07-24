# Pull Request Template

Copy this template structure when creating a Pull Request for CieloPass.

---

```markdown
## 📋 Task Header

**Type:** Feature, Tech, or Bug
**Priority:** Critical, High, Medium, or Low

---

## 🎯 What Changed

[Provide a short high-level summary of what changed and why it matters. Focus on reviewer-facing outcomes.]

### [Choose one task-type section]

#### Feature Additions

- [New feature or user-facing behavior]
- [Integration point or supporting change]
- [Validation or quality note]

**Impact:** [Describe the user-facing or product impact.]

#### Technical Improvements

**Before:**

- [Previous state or limitation]

**After:**

- [Improved state]

**Impact:** [Describe the technical or architecture impact.]

#### Bug Fix Details

**Issue:** [What was broken]

**Root Cause:** [Why it happened]

**Solution:** [How it was fixed]

**Impact:** [Describe the practical effect of the fix.]

---

## 🔑 Key Technical Decisions

1. **[Decision]** -> [Why this approach was chosen]
2. **[Decision]** -> [Why this approach was chosen]

---

## 📁 Files Created/Modified

- `app/src/main/java/com/cielo/cielopass/core/...` - [What changed]
- `app/src/main/java/com/cielo/cielopass/features/...` - [What changed]

---

## ✅ Definition of Done

- [x] Tested locally and verified unit tests (`./gradlew test`)
- [x] Passed static analysis formatting (`./gradlew ktlintCheck`)
- [x] Verified build compilation (`./gradlew assembleDebug`)

---

## 🔗 References

- **Documentation:** [Relevant doc or file path]
```

---

## PR Template Quality Checks

- [ ] Title follows `type(scope): subject` with valid type and scope
- [ ] Exactly one task-type section remains in the final PR body
- [ ] "What Changed" is concise and reviewer-focused
- [ ] Files section lists actual changed files with concrete paths
- [ ] Definition of Done checklist is accurate
- [ ] Final PR body is written in English only
