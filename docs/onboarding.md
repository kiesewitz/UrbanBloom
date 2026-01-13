# Developer Onboarding Guide - UrbanBloom

Welcome to the UrbanBloom team! This guide will get you up and running in **less than 30 minutes**.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Initial Setup](#initial-setup)
3. [Role-Specific Setup](#role-specific-setup)
4. [Verification](#verification)
5. [Your First Task](#your-first-task)
6. [Team Communication](#team-communication)
7. [Development Workflow](#development-workflow)
8. [Getting Help](#getting-help)

---

## Prerequisites

### Required Software

**For All Developers:**
- [ ] **Git** 2.30+ - [Download](https://git-scm.com/)
- [ ] **VS Code** - [Download](https://code.visualstudio.com/)
- [ ] **GitHub Copilot** extension for VS Code - [Install](https://marketplace.visualstudio.com/items?itemName=GitHub.copilot)
- [ ] **Docker Desktop** (for local databases) - [Download](https://www.docker.com/products/docker-desktop)

**For Backend Developers:**
- [ ] **Java 17** (OpenJDK or Oracle JDK) - [Download](https://adoptium.net/)
- [ ] **Maven 3.8+** (or use included `mvnw`)
- [ ] **PostgreSQL 15+** (Docker recommended)

**For Frontend Developers (Mobile & Web):**
- [ ] **Flutter 3.16+** - [Download](https://flutter.dev/docs/get-started/install)
- [ ] **Dart 3.2+** (included with Flutter)
- [ ] **Android Studio** (for mobile) - [Download](https://developer.android.com/studio)
- [ ] **Xcode** (for iOS, macOS only) - [Download from App Store](https://apps.apple.com/app/xcode/id497799835)

**Optional but Recommended:**
- [ ] **Postman** or **Insomnia** (for API testing)
- [ ] **TablePlus** or **pgAdmin** (for database management)

---

## Initial Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/your-org/UrbanBloom.git
cd UrbanBloom
```

### Step 2: Open in VS Code

```bash
code .
```

**Select Workspace**: When prompted, open the appropriate workspace file:
- Backend developers: `urbanbloom-server.code-workspace`
- Mobile developers: `urbanbloom-mobile.code-workspace`
- Web developers: `urbanbloom-web.code-workspace`
- Full-stack: `urbanbloom-full.code-workspace`

### Step 3: Install VS Code Extensions

VS Code will prompt you to install recommended extensions. Click **Install All**.

**Key Extensions**:
- GitHub Copilot (required)
- GitLens (highly recommended)
- Language-specific: Java Extension Pack, Flutter, Dart

### Step 4: Review Global Standards

Read the global coding standards:
```bash
open .github/copilot-instructions.md
```

**Key Takeaways**:
- Backend uses Domain-Driven Design (DDD)
- Frontend uses Component-Driven Design (CDD) with Atomic Design
- OpenAPI spec is the Single Source of Truth for API
- Conventional Commits for git messages

---

## Role-Specific Setup

### For Backend Developers

#### 1. Read Backend Instructions
```bash
open .github/instructions/backend-instructions.md
```

#### 2. Start PostgreSQL Database
```bash
docker run -d \
  --name urbanbloom-postgres \
  -p 5432:5432 \
  -e POSTGRES_DB=urbanbloom \
  -e POSTGRES_USER=urbanbloom \
  -e POSTGRES_PASSWORD=secret \
  postgres:15
```

#### 3. Initialize Server Project

If `server/` directory is empty, run:
```bash
./scripts/init-server.sh
```

This creates the Spring Boot project with DDD structure.

#### 4. Build and Run

```bash
cd server
mvn clean install
mvn spring-boot:run
```

**Verify**: Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

#### 5. Run Tests

```bash
mvn test
```

---

### For Mobile Developers

#### 1. Read Mobile Instructions
```bash
open .github/instructions/frontend-mobile-instructions.md
```

#### 2. Verify Flutter Installation
```bash
flutter doctor
```

Fix any issues reported by `flutter doctor`.

#### 3. Initialize Mobile Project

If `mobile/` directory is empty, run:
```bash
./scripts/init-mobile.sh
```

This creates the Flutter mobile project with Atomic Design structure.

#### 4. Get Dependencies

```bash
cd mobile
flutter pub get
```

#### 5. Run on Device/Simulator

**iOS Simulator** (macOS only):
```bash
open -a Simulator
flutter run
```

**Android Emulator**:
```bash
# Start emulator from Android Studio or:
emulator -avd <your_avd_name>
flutter run
```

**Physical Device**:
```bash
flutter devices  # List connected devices
flutter run -d <device_id>
```

#### 6. Run Tests

```bash
flutter test
```

---

### For Web Developers

#### 1. Read Web Instructions
```bash
open .github/instructions/frontend-web-instructions.md
```

#### 2. Verify Flutter Installation
```bash
flutter doctor
```

#### 3. Initialize Web Project

If `admin-web/` directory is empty, run:
```bash
./scripts/init-web-admin.sh
```

#### 4. Get Dependencies

```bash
cd admin-web
flutter pub get
```

#### 5. Run in Browser

```bash
flutter run -d chrome
```

**Verify**: App opens in Chrome at [http://localhost:xxxxx](http://localhost:xxxxx)

#### 6. Run Tests

```bash
flutter test
```

---

### For Project Managers

#### 1. Read PM Instructions
```bash
open .github/agents/project-manager.agent.md
```

#### 2. Familiarize with Workflow Prompts
```bash
open docs/prompts/workflow-prompts.md
```

#### 3. Review Issue Templates
```bash
ls .github/ISSUE_TEMPLATE/
```

#### 4. Set Up Project Board

1. Go to GitHub repository
2. Navigate to **Projects** tab
3. Review existing Scrumban board or create new one
4. Familiarize yourself with columns: Backlog, Ready, In Progress, Review, Done

---

## Verification

### Backend Verification Checklist

- [ ] PostgreSQL running and accessible
- [ ] Server builds successfully (`mvn clean install`)
- [ ] Server runs without errors (`mvn spring-boot:run`)
- [ ] Swagger UI accessible at http://localhost:8080/swagger-ui.html
- [ ] Tests pass (`mvn test`)

### Frontend Mobile Verification Checklist

- [ ] Flutter doctor shows no critical issues
- [ ] Dependencies installed (`flutter pub get` succeeds)
- [ ] App runs on simulator/device (`flutter run` succeeds)
- [ ] Tests pass (`flutter test`)
- [ ] Hot reload works (change code, save, see update)

### Frontend Web Verification Checklist

- [ ] Dependencies installed (`flutter pub get` succeeds)
- [ ] App runs in Chrome (`flutter run -d chrome` succeeds)
- [ ] Tests pass (`flutter test`)
- [ ] Hot reload works

---

## Your First Task

### Step 1: Find a Good First Issue

1. Go to GitHub Issues
2. Filter by label: `good-first-issue`
3. Pick an issue that matches your role (backend, mobile, web)

**Example Good First Issues**:
- Backend: "Add validation to Email value object"
- Mobile: "Create ActionButton Atom component"
- Web: "Add CSV export button to actions table"
- PM: "Update README with new onboarding instructions"

### Step 2: Assign Issue to Yourself

Comment on the issue: "I'll work on this!"

### Step 3: Create Feature Branch

```bash
git checkout -b feature/issue-number-short-description
# Example: git checkout -b feature/42-email-validation
```

### Step 4: Use AI Agents to Implement

Open VS Code, press `Cmd+I` (Mac) or `Ctrl+I` (Windows/Linux), and use the appropriate agent:

**Backend Example**:
```
@backend-ddd Create Email value object in domain/user/ package with validation regex and normalization to lowercase.
```

**Mobile Example**:
```
@mobile-cdd Create ActionButton Atom with variants primary/secondary/danger using AppColors.
```

**Web Example**:
```
@web-admin-cdd Add CSV export button to ActionsTable that exports all filtered actions.
```

### Step 5: Test Your Changes

**Backend**:
```bash
mvn test
```

**Frontend**:
```bash
flutter test
```

### Step 6: Commit with Conventional Commits

```bash
git add .
git commit -m "feat(user): add email value object with validation

- Add Email record class in domain/user/
- Validate email format with regex
- Normalize to lowercase
- Add unit tests

Closes #42"
```

### Step 7: Push and Create Pull Request

```bash
git push origin feature/42-email-validation
```

Go to GitHub and create a Pull Request:
- Use the PR template (auto-filled)
- Request review from a team member
- Link the issue (#42)

### Step 8: Address Review Comments

Reviewers may request changes. Make updates, commit, and push:

```bash
git add .
git commit -m "fix: update email regex to allow plus sign"
git push
```

### Step 9: Merge

Once approved, **squash and merge** the PR.

### Step 10: Celebrate! 🎉

You've completed your first task! Move on to more complex issues.

---

## Team Communication

### Daily Standup

**When**: Every day at 10:00 AM (your timezone)  
**Where**: Slack #standup or Zoom (link in calendar)  
**Format**: 3 questions:
1. What did you complete yesterday?
2. What will you work on today?
3. Any blockers?

**Duration**: 15 minutes max

### Code Reviews

- **Response Time**: Within 24 hours
- **Approval Required**: At least 1 team member
- **Be Constructive**: Suggest improvements, don't just criticize
- **Use Checklists**: See `.github/PULL_REQUEST_TEMPLATE.md`

### Channels

**Slack/Discord Channels**:
- `#general` - General discussion
- `#backend` - Backend development
- `#frontend` - Mobile and web development
- `#standup` - Daily standup updates
- `#qa` - Testing and quality assurance
- `#random` - Off-topic fun

**Emergency**: For critical issues (production down, security breach), use `@here` in `#general`.

---

## Development Workflow

### 1. Pick an Issue

From GitHub Issues, choose an issue labeled:
- `ready` (refined and ready to work on)
- Your role label (`backend`, `frontend-mobile`, `frontend-web`, `pm`)
- Appropriate priority (`p0` critical, `p1` high, `p2` medium, `p3` low)

### 2. Create Branch

```bash
git checkout main
git pull origin main
git checkout -b feature/issue-number-description
```

**Branch Naming**:
- Feature: `feature/123-add-leaderboard`
- Bugfix: `fix/456-null-pointer-action-service`
- Refactor: `refactor/789-extract-email-validation`

### 3. Develop with AI Agents

Use specialized agents (see `docs/ai-environment-guide.md`):
- `@backend-ddd` for domain modeling
- `@backend-business-logic` for use cases
- `@mobile-cdd` for mobile UI
- `@web-admin-cdd` for web UI
- `@project-manager` for planning
- `@documentation` for docs

### 4. Test Locally

**Backend**:
```bash
mvn test                    # Unit tests
mvn verify                  # Integration tests
mvn spring-boot:run         # Manual testing
```

**Frontend**:
```bash
flutter test                      # Unit/widget tests
flutter run                       # Manual testing
flutter test integration_test/    # Integration tests (if exist)
```

### 5. Commit Often

Commit after each logical change:
```bash
git add .
git commit -m "feat(domain): add action aggregate"
```

Use **Conventional Commits**:
- `feat:` New feature
- `fix:` Bug fix
- `refactor:` Code refactoring
- `docs:` Documentation
- `test:` Tests
- `chore:` Tooling, dependencies

### 6. Push and Create PR

```bash
git push origin feature/123-add-leaderboard
```

Go to GitHub → **New Pull Request** → Fill template → Request review.

### 7. Respond to Reviews

Address feedback promptly. Update code, commit, push.

### 8. Merge

Once approved:
- **Squash and merge** (keeps main clean)
- Delete branch after merge

### 9. Move Issue to Done

Drag issue to **Done** column on project board.

---

## Getting Help

### Documentation Resources

- **AI Environment Guide**: `docs/ai-environment-guide.md` (how to use agents)
- **Architecture**: `docs/architecture.md` (system design)
- **Backend Instructions**: `.github/instructions/backend-instructions.md`
- **Mobile Instructions**: `.github/instructions/frontend-mobile-instructions.md`
- **Web Instructions**: `.github/instructions/frontend-web-instructions.md`
- **Domain Model**: `shared-resources/documentation/domain-model-description-urbanbloom.md`

### Ask for Help

**Don't Hesitate!** Everyone was new once.

1. **Search Documentation**: Check if answer is in docs
2. **Ask in Slack**: Use appropriate channel (`#backend`, `#frontend`)
3. **Tag a Teammate**: Mention someone knowledgeable (`@jane`)
4. **Schedule a Call**: For complex issues, hop on a Zoom call

### Pair Programming

Request a pairing session for:
- Complex features
- Learning new patterns
- Debugging tricky issues

**How**: Message in Slack: "Anyone available for pairing on [topic]?"

---

## Tips for Success

### ✅ DO:
- **Commit often**: Small, focused commits
- **Test your code**: Don't rely only on reviewers
- **Ask questions**: No question is too small
- **Use AI agents**: They accelerate development
- **Review others' code**: You learn by reviewing
- **Update docs**: Keep documentation current

### ❌ DON'T:
- **Work on main**: Always create a branch
- **Push untested code**: Test locally first
- **Ignore CI failures**: Fix them immediately
- **Skip code review**: Even "small" changes need review
- **Hoard knowledge**: Share what you learn

---

## Onboarding Checklist

### Week 1: Setup & Exploration
- [ ] Set up development environment
- [ ] Review global coding standards
- [ ] Read role-specific instructions
- [ ] Complete "Your First Task" section
- [ ] Introduce yourself in `#general` Slack channel
- [ ] Schedule 1:1 with your mentor/manager

### Week 2: First Real Feature
- [ ] Pick a feature issue (not good-first-issue)
- [ ] Use AI agents to implement
- [ ] Write tests (unit + integration)
- [ ] Create PR and iterate on feedback
- [ ] Merge your first feature!
- [ ] Attend sprint planning meeting

### Week 3: Deeper Integration
- [ ] Review 3+ PRs from teammates
- [ ] Pair program with a teammate
- [ ] Contribute to documentation (fix typos, add examples)
- [ ] Complete a user story end-to-end (if full-stack) or cross-domain feature
- [ ] Attend retrospective meeting

### Week 4: Full Team Member
- [ ] Lead a feature implementation
- [ ] Mentor another developer on a task
- [ ] Suggest a process improvement
- [ ] Present a demo in sprint review (optional)

---

## Next Steps

You're all set! 🚀

1. **Review**: Read `docs/ai-environment-guide.md` to master AI-assisted development
2. **Explore**: Browse the codebase to understand structure
3. **Build**: Start working on your first issue
4. **Connect**: Introduce yourself to the team

**Welcome to UrbanBloom!** Let's build something amazing together. 🌱

---

*Questions? Reach out in `#general` on Slack or email your team lead.*
