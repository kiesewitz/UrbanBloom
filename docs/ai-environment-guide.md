# AI-Assisted Development Environment Guide - UrbanBloom

Welcome to the UrbanBloom AI-assisted development environment! This guide will help you understand and effectively use the specialized AI agents, instructions, and prompts to accelerate your development workflow.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Agent Overview](#agent-overview)
3. [How to Use Agents](#how-to-use-agents)
4. [Workflow Examples](#workflow-examples)
5. [Prompt Best Practices](#prompt-best-practices)
6. [Handoff Patterns](#handoff-patterns)
7. [Troubleshooting](#troubleshooting)

---

## Quick Start

### Prerequisites
- **VS Code** with **GitHub Copilot** extension installed
- **Git** for version control
- Clone the UrbanBloom repository

### First-Time Setup
1. **Review Global Standards**: Read `.github/copilot-instructions.md` for coding conventions
2. **Identify Your Role**: Backend, Frontend Mobile, Frontend Web, or Project Manager
3. **Read Role Instructions**: See `.github/instructions/[your-role]-instructions.md`
4. **Initialize Your Project** (if starting fresh):
   - Backend: `./scripts/init-server.sh`
   - Mobile: `./scripts/init-mobile.sh`
   - Web: `./scripts/init-web-admin.sh`

### Your First AI-Assisted Task
1. Open a file in your domain (e.g., backend, mobile, web)
2. Press `Cmd+I` (Mac) or `Ctrl+I` (Windows/Linux) to open Copilot Chat
3. Select the appropriate agent (e.g., `@backend-ddd`)
4. Ask a question or request code generation

---

## Agent Overview

UrbanBloom has **6 specialized agents** designed for different aspects of development:

### 1. Backend DDD Agent (`@backend-ddd`)
**Location**: `.github/agents/backend-ddd.agent.md`  
**Purpose**: Domain modeling with Domain-Driven Design patterns  
**Use For**:
- Creating aggregates, entities, value objects
- Designing domain services
- Modeling domain events
- Creating repository interfaces

**Example Usage**:
```
@backend-ddd Create an Aggregate Root for the Action domain with properties: id, title, description, creatorId, status, pointsEarned. Business rule: Actions can only be verified by someone other than the creator.
```

---

### 2. Backend Business Logic Agent (`@backend-business-logic`)
**Location**: `.github/agents/backend-business-logic.agent.md`  
**Purpose**: Implementing use cases and API endpoints  
**Use For**:
- Implementing application services (use cases)
- Creating REST controllers
- Writing integration tests
- Optimizing queries and performance

**Example Usage**:
```
@backend-business-logic Implement the use case "User documents green action" that creates an action, publishes ActionCreated event, and awards initial points to the user.
```

---

### 3. Mobile CDD Agent (`@mobile-cdd`)
**Location**: `.github/agents/mobile-cdd.agent.md`  
**Purpose**: Flutter mobile development with Component-Driven Design  
**Use For**:
- Creating UI components (Atomic Design: Atoms → Pages)
- Implementing Riverpod providers
- Integrating with API
- Offline-first data handling

**Example Usage**:
```
@mobile-cdd Create an Organism component ActionCard that displays an action with image, title, description, points, and date. Make it tappable to navigate to details.
```

---

### 4. Web Admin CDD Agent (`@web-admin-cdd`)
**Location**: `.github/agents/web-admin-cdd.agent.md`  
**Purpose**: Flutter Web admin panel development  
**Use For**:
- Creating data tables with filtering/sorting
- Building dashboards with KPI cards
- Implementing analytics charts
- CSV export functionality

**Example Usage**:
```
@web-admin-cdd Create a data table for managing actions with columns: ID, Title, User, Status, Points, Created. Include filtering by status and CSV export.
```

---

### 5. Project Manager Agent (`@project-manager`)
**Location**: `.github/agents/project-manager.agent.md`  
**Purpose**: Project management tasks and planning  
**Use For**:
- Converting notes to user stories
- Breaking epics into tasks
- Estimating story points
- Generating sprint plans

**Example Usage**:
```
@project-manager Convert these meeting notes into a user story with acceptance criteria and domain assignments: "Users want to see a leaderboard showing top contributors by city district."
```

---

### 6. Documentation Agent (`@documentation`)
**Location**: `.github/agents/documentation.agent.md`  
**Purpose**: Maintaining project documentation  
**Use For**:
- Updating README files
- Generating architecture diagrams
- Creating API documentation
- Writing release notes

**Example Usage**:
```
@documentation Update docs/architecture.md with a Mermaid sequence diagram showing the flow when a user creates an action (mobile app → API → database → domain events → gamification).
```

---

## How to Use Agents

### Method 1: GitHub Copilot Chat in VS Code

1. **Open Copilot Chat**: `Cmd+I` (Mac) or `Ctrl+I` (Windows/Linux)
2. **Reference Agent**: Type `@agent-name` (e.g., `@backend-ddd`)
3. **Provide Context**: Describe what you need
4. **Iterate**: Refine the output by asking follow-up questions

**Tip**: Agents have access to the current file you're editing, so open the relevant file first for better context.

### Method 2: Prompt Catalogs

Use pre-written prompts from `docs/prompts/`:
- **Workflow Prompts**: `workflow-prompts.md` (PM tasks, issue creation, retrospectives)
- **Backend Prompts**: `backend-prompts.md` (domain modeling, API design, testing)
- **Frontend Prompts**: `frontend-prompts.md` (component design, state management, testing)

**How to Use**:
1. Open the relevant prompt catalog file
2. Find the prompt that matches your task
3. Copy the prompt template
4. Fill in the placeholders `[LIKE_THIS]`
5. Paste into Copilot Chat with the appropriate agent

### Method 3: Instructions Files

Instructions files provide guidance but don't require agent invocation:
- **Backend Instructions**: `.github/instructions/backend-instructions.md`
- **Frontend Mobile Instructions**: `.github/instructions/frontend-mobile-instructions.md`
- **Frontend Web Instructions**: `.github/instructions/frontend-web-instructions.md`

These are **reference documents**—read them to understand patterns, conventions, and best practices.

---

## Workflow Examples

### Example 1: Backend Developer Implements User Story

**User Story**: "As a user, I want to document a green action so that I can earn points."

**Workflow**:

1. **Domain Modeling** (Agent: `@backend-ddd`)
   ```
   @backend-ddd Create Action aggregate in the Action bounded context with properties: id (ActionId), title, description, photoUrl, creatorId (UserId), status (PENDING/VERIFIED), pointsEarned, createdAt. Business rule: Actions start as PENDING and require verification to earn points.
   ```
   → Agent generates `Action.java` aggregate, `ActionId` value object, `ActionStatus` enum, `ActionRepository` interface, `ActionCreated` domain event.

2. **API Design** (Prompt: Backend Prompts #5)
   - Open `docs/prompts/backend-prompts.md`
   - Use "Design RESTful Endpoint" prompt
   - Update `openapi/urbanbloom-api-v1.yaml` with new endpoint

3. **Use Case Implementation** (Agent: `@backend-business-logic`)
   ```
   @backend-business-logic Implement the use case CreateActionUseCase that:
   1. Validates input (title, description, creatorId)
   2. Creates Action aggregate
   3. Saves to repository
   4. Publishes ActionCreated event
   5. Returns ActionDTO
   ```
   → Agent generates `CreateActionUseCase.java`, `CreateActionCommand.java`, `ActionDTO.java`.

4. **REST Controller** (Agent: `@backend-business-logic`)
   ```
   @backend-business-logic Create REST controller for POST /api/v1/actions matching the OpenAPI spec. Use CreateActionUseCase.
   ```
   → Agent generates `ActionController.java`.

5. **Testing** (Prompt: Backend Prompts #13)
   ```
   @backend-business-logic Generate integration test for CreateActionUseCase with TestContainers.
   ```

6. **Commit** with conventional commit message:
   ```bash
   git add .
   git commit -m "feat(action): implement action creation use case

   - Add Action aggregate with business rules
   - Implement CreateActionUseCase
   - Add POST /actions endpoint
   - Publish ActionCreated domain event

   Closes #42"
   ```

---

### Example 2: Mobile Developer Builds Action List Screen

**Task**: Create a page that lists recent actions with infinite scroll.

**Workflow**:

1. **Data Provider** (Agent: `@mobile-cdd`)
   ```
   @mobile-cdd Create a Riverpod FutureProvider that fetches recent actions from ActionRepository with pagination (20 items per page).
   ```
   → Agent generates `recent_actions_provider.dart`.

2. **Component Design** (Prompt: Frontend Prompts #3)
   ```
   @mobile-cdd Create an Organism ActionCard that displays action details: photo (if available), title, description (2 lines max), points earned, and relative date. Make it tappable.
   ```
   → Agent generates `lib/ui/organisms/action_card.dart`.

3. **Page Implementation** (Agent: `@mobile-cdd`)
   ```
   @mobile-cdd Create ActionListPage that:
   - Fetches actions with recentActionsProvider
   - Displays actions in ListView.builder
   - Implements pull-to-refresh
   - Implements infinite scroll (load more at bottom)
   - Shows loading/error states
   - Navigates to ActionDetailsPage on card tap
   ```
   → Agent generates `lib/ui/pages/action_list_page.dart`.

4. **Testing** (Prompt: Frontend Prompts #17)
   ```
   @mobile-cdd Generate widget test for ActionCard with test data.
   ```

5. **Commit**:
   ```bash
   git add .
   git commit -m "feat(mobile): add action list page with infinite scroll

   - Create ActionCard organism component
   - Implement ActionListPage with pagination
   - Add pull-to-refresh and infinite scroll
   - Handle loading and error states

   Closes #45"
   ```

---

### Example 3: PM Creates Sprint Backlog

**Task**: Plan next 2-week sprint.

**Workflow**:

1. **Sprint Planning** (Prompt: Workflow Prompts #7)
   ```
   @project-manager Generate a sprint backlog for the next 2 weeks:

   Team Capacity:
   - Backend: 10 person-days (2 devs * 5 days)
   - Frontend Mobile: 5 person-days (1 dev * 5 days)
   - Frontend Web: 5 person-days (1 dev * 5 days)

   Priorities:
   1. Action verification workflow (high)
   2. Leaderboard display (high)
   3. Challenge system (medium)

   Available issues: [Link to GitHub issues labeled "ready"]
   ```
   → Agent generates sprint plan with balanced workload.

2. **User Story Refinement** (Prompt: Workflow Prompts #1)
   ```
   @project-manager Convert these meeting notes into structured user stories:
   "We need a way for city admins to verify actions. They should see a list of pending actions with photos, and approve or reject them. Approved actions award points to users."
   ```
   → Agent creates GitHub issue-ready markdown.

3. **Create Issues** in GitHub with generated content.

4. **Sprint Kickoff**: Share sprint plan with team in Slack/Discord.

---

## Prompt Best Practices

### ✅ DO: Be Specific
```
❌ BAD: Create a user component
✅ GOOD: Create a UserProfileCard Molecule that displays user avatar (40px), display name, and point total. Use AppColors and AppSpacing.
```

### ✅ DO: Provide Context
```
❌ BAD: Add validation
✅ GOOD: Add validation to Email value object: must match regex ^[\w.-]+@[\w.-]+\.[a-z]{2,}$, normalize to lowercase, throw IllegalArgumentException if invalid.
```

### ✅ DO: Reference Existing Patterns
```
❌ BAD: Make an API call
✅ GOOD: Create a repository implementation for Challenge following the offline-first pattern from ActionRepository: read from cache, fetch from API, update cache, handle errors.
```

### ✅ DO: Specify Output Format
```
❌ BAD: Document this decision
✅ GOOD: Create an ADR (Architecture Decision Record) for choosing Riverpod over Bloc with format: Title, Status, Context, Decision, Alternatives Considered, Consequences (positive/negative/neutral).
```

### ❌ DON'T: Be Vague
```
❌ BAD: Fix the bug
✅ GOOD: Fix NullPointerException in ActionService.verifyAction() when verifier is not found. Add null check and throw VerifierNotFoundException.
```

### ❌ DON'T: Assume Context
```
❌ BAD: Use the same pattern
✅ GOOD: Use the same DDD pattern as UserAggregate: Aggregate Root with value objects, domain events, and repository interface in domain package.
```

---

## Handoff Patterns

Agents work together through **handoffs**. Here's how they coordinate:

### Pattern 1: Backend DDD → Backend Business Logic
**Scenario**: Domain model created, now implement use case.

**Backend DDD Agent** creates:
- `Action.java` (Aggregate)
- `ActionRepository.java` (Interface)
- `ActionCreated.java` (Domain Event)

**Handoff to Backend Business Logic Agent**:
```
@backend-business-logic Now implement CreateActionUseCase using the Action aggregate and ActionRepository. Accept CreateActionCommand with title, description, creatorId. Return ActionDTO.
```

---

### Pattern 2: Backend → OpenAPI → Frontend
**Scenario**: New API endpoint implemented, frontend needs to consume it.

**Backend Business Logic Agent** implements:
- `POST /api/v1/actions` endpoint
- Updates `openapi/urbanbloom-api-v1.yaml`

**Handoff to Mobile CDD Agent**:
```
@mobile-cdd Generate API client for Action endpoints from openapi/urbanbloom-api-v1.yaml. Then create ActionRepository implementation using the generated client with offline-first pattern.
```

---

### Pattern 3: Mobile/Web → Documentation
**Scenario**: Major feature completed, docs need updating.

**Mobile CDD Agent** implements:
- Action list page with components

**Handoff to Documentation Agent**:
```
@documentation Update README.md with new features section describing the action list page: infinite scroll, pull-to-refresh, offline support.
```

---

### Pattern 4: PM → Backend → Frontend
**Scenario**: User story needs implementation across layers.

**Project Manager Agent** creates:
- User story with acceptance criteria
- Domain assignments

**Handoff to Backend DDD Agent**:
```
@backend-ddd Model the domain for user story #123: "As a user, I want to participate in challenges." Create Challenge aggregate with properties and business rules from acceptance criteria.
```

**Then handoff to Mobile CDD Agent**:
```
@mobile-cdd Create ChallengePage that displays active challenges, allows users to join, and shows progress toward challenge goals.
```

---

## Troubleshooting

### Issue: Agent Doesn't Understand Context
**Solution**: Open the relevant file first, or paste the file content in the chat.

**Example**:
```
# Open Action.java first, then:
@backend-ddd Add a method verifyAction(UserId verifier) that checks business rules and publishes ActionVerified event.
```

---

### Issue: Generated Code Doesn't Follow Conventions
**Solution**: Reference the instructions file explicitly.

**Example**:
```
@backend-ddd Create User aggregate following the DDD patterns in .github/instructions/backend-instructions.md: pure domain layer, no JPA annotations, value objects for Email and UserId.
```

---

### Issue: Agent Output Is Too Generic
**Solution**: Be more specific, provide examples, reference existing code.

**Example**:
```
❌ Generic: Create a button
✅ Specific: Create ActionButton Atom similar to CancelButton but with variant=primary, uses AppColors.primary background, and accepts isLoading prop to show CircularProgressIndicator.
```

---

### Issue: Need to Switch Agents Mid-Task
**Solution**: Use handoff patterns. Finish current subtask, then invoke next agent.

**Example**:
```
# After @backend-ddd creates domain model:
@backend-business-logic Now implement the use case using the Action aggregate created above. [Paste Action.java or reference it]
```

---

### Issue: Agent Hallucinates Non-Existent Code
**Solution**: Verify agent references actual files. Check file paths.

**Example**:
```
# Verify first:
@documentation Does ActionRepository exist in src/main/java/com/urbanbloom/backend/domain/action/? If not, ask @backend-ddd to create it first.
```

---

## Advanced Tips

### Tip 1: Chain Multiple Agents
For complex tasks, chain agents in sequence:

```
1. @project-manager: Create user story
2. @backend-ddd: Model domain
3. @backend-business-logic: Implement use case
4. @mobile-cdd: Build UI
5. @documentation: Update docs
```

### Tip 2: Use Prompt Catalogs as Templates
Don't reinvent the wheel—start with proven prompts:

```
# Copy prompt from docs/prompts/backend-prompts.md #12
# Modify placeholders
# Paste into Copilot Chat
```

### Tip 3: Combine Agents with Manual Review
Agents accelerate development but aren't perfect:

1. Generate code with agent
2. **Review** for correctness, edge cases, performance
3. **Test** (unit, integration, e2e)
4. **Refine** if needed
5. Commit

### Tip 4: Keep Context Windows Focused
Agents work best with focused context:

- ❌ Don't: Open 10 files and ask agent to "fix everything"
- ✅ Do: Open 1-2 related files and ask specific question

### Tip 5: Update Agents as Project Evolves
Agents are markdown files—update them when patterns change:

```bash
# Add new pattern to agent
vim .github/agents/backend-ddd.agent.md

# Commit
git commit -m "docs(agent): add pattern for handling domain events with saga"
```

---

## Additional Resources

### Documentation
- **Architecture**: `docs/architecture.md`
- **Onboarding**: `docs/onboarding.md`
- **Domain Model**: `shared-resources/documentation/domain-model-description-urbanbloom.md`

### Agents
- All agents: `.github/agents/`
- Instructions: `.github/instructions/`
- Prompts: `docs/prompts/`

### Community
- **GitHub Discussions**: Ask questions, share tips
- **Slack/Discord**: Real-time team communication

---

## Summary

**AI-assisted development in UrbanBloom**:
1. **6 specialized agents** for different roles
2. **Role-specific instructions** for conventions
3. **Prompt catalogs** with proven templates
4. **Handoff patterns** for agent coordination
5. **Initialization scripts** for project setup

**Key to Success**:
- ✅ Be specific in prompts
- ✅ Provide context (files, examples, references)
- ✅ Review and test generated code
- ✅ Use handoffs for complex tasks
- ✅ Update agents as project evolves

**Get Started**: Open a file, press `Cmd+I`, select an agent, and start building! 🚀

---

*Last Updated: 2026-01-13*
