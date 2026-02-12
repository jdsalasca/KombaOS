Git workflow
- All development happens on feature/* branches from develop.
- Open PRs into develop with review.
- Merge PRs promptly; no partial work left unmerged.
- Do not mix refactors with functional changes.
- Master is for releases only.
- Master → develop backport happens via automated PR.

Do
- Use descriptive commits aligned with the change.
- Include summary, scope, and validation in PRs.
- State whether the work is fully merged into develop.

Don’t
- Don’t push directly to master.
- Don’t merge PRs without validation.
- Don’t leave feature branches unmerged.
