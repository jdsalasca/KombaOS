Backend
- Keep controllers thin and services focused on logic.
- Use clear DTOs for inputs and outputs.
- Validate at system boundaries.
- Avoid direct coupling between layers.

Do
- Handle errors with consistent exceptions.
- Keep stable contracts between layers.

Don’t
- Don’t mix persistence with business rules.
