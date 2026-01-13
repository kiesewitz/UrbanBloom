# Workflow Prompts Catalog - UrbanBloom

**Purpose**: Reusable prompt templates for common project management and workflow tasks  
**Target Users**: Project Managers, all team members  
**Usage**: Copy prompt, fill placeholders [in brackets], submit to GitHub Copilot/agent

---

## Issue Creation Prompts

### 1. Convert Meeting Notes to User Story

```
Convert these meeting notes into a structured GitHub user story:

[PASTE MEETING NOTES HERE]

Create a user story with:
- Title: Clear, user-focused (As a [role], I want [goal], so that [benefit])
- Description: Context and background
- Acceptance Criteria: Testable criteria (Given/When/Then format)
- Affected Domains: Which of the 9 bounded contexts are involved
- Story Points: Estimate complexity (1, 2, 3, 5, 8, 13)
- Technical Notes: Implementation hints or constraints
- Labels: Appropriate labels (feature, backend, frontend-mobile, frontend-web, etc.)

Format the output as markdown suitable for pasting into GitHub issue.
```

**Example Usage**:
```
Convert these meeting notes into a structured GitHub user story:

"Users want to see their rank on the leaderboard. It should show top 10 users and their position. Real-time updates would be nice. Cache the data to avoid performance issues."

[Agent generates full user story with acceptance criteria, domain assignments, etc.]
```

---

### 2. Create Bug Report from Description

```
Create a detailed bug report from this description:

[PASTE BUG DESCRIPTION HERE]

Generate a bug report with:
- Title: Concise summary of the issue
- Steps to Reproduce: Numbered steps to recreate the bug
- Expected Behavior: What should happen
- Actual Behavior: What currently happens
- Impact: Severity and affected users
- Affected Components: Which modules/domains are broken
- Possible Root Cause: Initial hypothesis
- Related Issues: Link to similar issues (if any)
- Labels: bug, severity-[low/medium/high/critical], affected domains

Format as markdown for GitHub issue.
```

---

### 3. Refine Epic into Tasks

```
Break down this epic into implementable tasks:

Epic: [EPIC TITLE]
Description: [EPIC DESCRIPTION]
Affected Domains: [LIST DOMAINS]

Create 5-10 granular tasks with:
- Clear scope (completable in 1-3 days)
- Dependencies identified
- Domain assignment (backend, mobile, web, PM)
- Implementation order
- Acceptance criteria per task

Output as a list of GitHub issues (markdown format).
```

**Example**:
```
Break down this epic into implementable tasks:

Epic: Gamification System
Description: Users earn points and badges for green actions. Leaderboard shows top contributors.
Affected Domains: Gamification, User, Action

[Agent generates 8 tasks: 1) Domain model, 2) Points calculation, 3) Badge system, 4) Leaderboard API, 5) Mobile UI, etc.]
```

---

### 4. Create Technical Spike Task

```
Create a technical spike task to research: [TOPIC]

Context: [WHY WE NEED TO RESEARCH THIS]

Generate spike task with:
- Title: "Spike: Research [topic]"
- Investigation Questions: What we need to find out
- Success Criteria: What deliverables prove the spike is complete
- Time Box: Maximum time to spend (1-2 days)
- Output: Document, proof of concept, or recommendation
- Labels: spike, [relevant domain]

Format for GitHub issue.
```

---

## Pull Request Prompts

### 5. Generate PR Description from Git Diff

```
Generate a comprehensive PR description from this git diff:

[PASTE GIT DIFF OR LINK TO PR]

Include:
- Summary: High-level overview of changes
- Changes Made: Bulleted list of modifications
- Affected Domains: Which bounded contexts were changed
- Breaking Changes: Any API or behavior changes (if applicable)
- Testing: How changes were tested
- Screenshots: Placeholder for UI changes (if applicable)
- Checklist:
  - [ ] Tests added/updated
  - [ ] Documentation updated
  - [ ] OpenAPI spec updated (if API changes)
  - [ ] Follows coding standards

Format for GitHub PR description.
```

---

### 6. Code Review Checklist Generator

```
Generate a code review checklist for this PR:

PR: [LINK OR DESCRIPTION]
Changed Files: [LIST FILES OR FILE TYPES]
Domains Affected: [LIST DOMAINS]

Create checklist covering:
- Code Quality: Naming, readability, complexity
- Architecture: DDD/CDD patterns followed, boundaries respected
- Testing: Adequate test coverage, test quality
- Performance: No N+1 queries, proper caching, optimizations
- Security: Input validation, SQL injection prevention, auth checks
- Documentation: Comments, README updates, API docs
- OpenAPI Compliance: Endpoints match spec (if backend)
- Accessibility: WCAG compliance (if frontend)

Output as markdown checklist for review comment.
```

---

## Sprint Planning Prompts

### 7. Generate Sprint Backlog

```
Generate a sprint backlog for the next 2 weeks:

Team Capacity:
- Backend: [X] person-days
- Frontend Mobile: [Y] person-days
- Frontend Web: [Z] person-days
- PM: [W] person-days

Available Issues: [LINK TO GITHUB ISSUES OR DESCRIBE]

Priorities: [HIGH PRIORITY FEATURES/BUGS]

Create sprint plan with:
- Issues assigned to each team member
- Balanced workload across team
- Dependencies and blockers identified
- Sprint goal summary
- Estimated velocity (story points)

Output as markdown table with columns: Issue, Assignee, Story Points, Priority, Dependencies.
```

---

### 8. Sprint Retrospective Facilitation

```
Facilitate a sprint retrospective based on these inputs:

Sprint: [SPRINT NUMBER/NAME]
Completed: [X] story points
Team Feedback: [PASTE FEEDBACK OR NOTES]

Generate retrospective document with sections:
1. What Went Well: Positive highlights
2. What Didn't Go Well: Challenges and blockers
3. Root Cause Analysis: Why challenges occurred
4. Action Items: Concrete steps to improve (assigned to individuals)
5. Shoutouts: Team member recognition

Format as markdown for documentation.
```

---

### 9. Lessons Learned Template

```
Create a lessons learned document for: [PROJECT PHASE/MILESTONE]

Context: [DESCRIBE WHAT WAS COMPLETED]
Outcomes: [SUCCESS METRICS]
Challenges: [MAJOR ISSUES ENCOUNTERED]

Generate document with:
- Executive Summary: High-level takeaways
- Successes: What worked well and why
- Challenges: What didn't work and why
- Key Learnings: Technical and process insights
- Recommendations: Actions for future phases
- Metrics: Quantitative results (velocity, quality, etc.)

Output as markdown suitable for docs/ folder.
```

---

### 10. Post-Mortem Template

```
Create a post-mortem for this incident:

Incident: [BRIEF DESCRIPTION]
Impact: [AFFECTED USERS/SYSTEMS]
Timeline: [KEY EVENTS]

Generate post-mortem with:
- Incident Summary: What happened, when, and impact
- Timeline: Chronological sequence of events
- Root Cause Analysis: Why it happened (5 Whys method)
- Contributing Factors: Other factors that made it worse
- Resolution: How it was fixed
- Preventive Measures: Actions to prevent recurrence
- Action Items: Concrete tasks (assigned, with deadlines)
- Lessons Learned: What we learned from this

Format as markdown for incident documentation.
```

---

## Documentation Prompts

### 11. Generate Changelog from Commits

```
Generate a changelog for release [VERSION] from these commits:

Commit Range: [START_COMMIT]...[END_COMMIT]
Or paste commit messages: [COMMITS]

Create changelog with:
- Version number and release date
- Categories: Features, Fixes, Performance, Breaking Changes, Documentation
- Group by bounded context (User, Action, Gamification, etc.)
- User-friendly descriptions (no technical jargon)
- Links to PRs/issues
- Migration guide (if breaking changes)

Output in Keep a Changelog format (markdown).
```

---

### 12. Generate Architecture Decision Record (ADR)

```
Document this architectural decision:

Decision: [WHAT WAS DECIDED]
Context: [WHY THE DECISION WAS NEEDED]
Alternatives: [OPTIONS CONSIDERED]

Generate ADR with format:
---
Title: [Number] - [Short Title]
Status: [Proposed/Accepted/Deprecated/Superseded]
Date: [YYYY-MM-DD]

## Context
[Situation, problem, and forces at play]

## Decision
[Chosen solution and rationale]

## Alternatives Considered
- [Option 1]: [Why rejected]
- [Option 2]: [Why rejected]

## Consequences

### Positive
- [Benefit 1]
- [Benefit 2]

### Negative
- [Drawback 1]
- [Drawback 2]

### Neutral
- [Other impact 1]

## Related ADRs
- [Links to related decisions]
---

Save as: docs/adr/[NNNN]-[title-slug].md
```

---

### 13. Update README After Feature Addition

```
Update the project README after adding this feature:

Feature: [FEATURE NAME]
Description: [WHAT IT DOES]
Affected Components: [BACKEND/MOBILE/WEB]

Suggest README updates:
- Features section: Add new feature
- Installation: Any new dependencies
- Usage: Example of using the feature
- API Documentation: Link to API docs (if applicable)
- Configuration: New env vars or config (if any)

Output as markdown diff or new README section.
```

---

## Estimation & Planning Prompts

### 14. Story Point Estimation

```
Estimate story points for this user story:

[PASTE USER STORY]

Consider:
- Complexity: Technical difficulty
- Uncertainty: Unknowns and risks
- Effort: Time required
- Dependencies: External dependencies

Provide:
- Estimated story points (1, 2, 3, 5, 8, 13)
- Reasoning: Why this estimate
- Risks: Factors that could increase effort
- Breakdown: Tasks involved

Use Fibonacci scale (1, 2, 3, 5, 8, 13, 21).
```

---

### 15. Risk Assessment

```
Assess technical risks for this feature:

Feature: [FEATURE NAME]
Description: [BRIEF DESCRIPTION]
Domains Affected: [LIST]

Identify risks with:
- Risk: Description of risk
- Probability: Low/Medium/High
- Impact: Low/Medium/High
- Mitigation: How to reduce risk
- Contingency: Plan if risk occurs

Output as markdown table.
```

---

## Onboarding Prompts

### 16. Generate Onboarding Checklist

```
Create an onboarding checklist for a new [ROLE] developer:

Role: [Backend/Frontend-Mobile/Frontend-Web/PM]

Generate checklist with:
- Prerequisites: Software to install
- Setup Steps: Environment configuration
- Verification: How to verify setup works
- First Tasks: Starter issues (good-first-issue)
- Resources: Documentation, tutorials, contacts
- Timeline: Expected timeframe for each step

Output as markdown checklist for docs/onboarding-[role].md.
```

---

### 17. Generate "Good First Issue" from Existing Task

```
Convert this task into a "good-first-issue" for new contributors:

Task: [TASK DESCRIPTION]

Create beginner-friendly issue with:
- Clear description: What needs to be done
- Context: Why it's needed
- Step-by-step guidance: How to approach it
- Resources: Links to relevant docs, examples
- Expected outcome: What success looks like
- Mentorship: Who to ask for help
- Labels: good-first-issue, [domain], [language]

Format for GitHub issue.
```

---

## Communication Prompts

### 18. Standup Summary Generator

```
Generate a standup summary from these team updates:

[PASTE TEAM MEMBER UPDATES]

Format:
Team Member: [Name]
- Yesterday: [What was completed]
- Today: [What will be worked on]
- Blockers: [Any issues]

Create summary with:
- Completed work highlights
- Today's focus
- Blockers that need addressing
- Team coordination needs

Output as markdown for Slack/Discord post.
```

---

### 19. Demo Script Generator

```
Create a demo script for this feature:

Feature: [FEATURE NAME]
Target Audience: [STAKEHOLDERS/USERS]
Duration: [X] minutes

Generate script with:
- Introduction: Context and value proposition
- Demo Steps: Screen-by-screen walkthrough
- Talking Points: Key features to highlight
- Q&A Prep: Anticipated questions and answers
- Conclusion: Next steps and feedback request

Output as presenter notes (markdown).
```

---

### 20. Release Announcement

```
Create a release announcement for version [VERSION]:

Release: [VERSION]
Highlights: [MAJOR FEATURES]
Changes: [LINK TO CHANGELOG OR LIST]

Generate announcement with:
- Headline: Exciting summary
- Overview: What's new in this release
- Highlights: Top 3-5 features with descriptions
- Improvements: Bug fixes and enhancements
- Breaking Changes: Migration guide (if any)
- Thank You: Contributors recognition
- Next Steps: How to upgrade, feedback channels

Output as markdown for blog post or email.
```

---

## Prompt Best Practices

### Writing Effective Prompts

1. **Be Specific**: Provide context, constraints, and desired format
2. **Include Examples**: Show what good output looks like
3. **Set Boundaries**: Specify what NOT to include
4. **Iterate**: Refine prompts based on agent output quality
5. **Use Placeholders**: [BRACKETS] for user-provided content

### Testing Prompts

1. **Test with Real Data**: Use actual user stories, commits, etc.
2. **Validate Output**: Ensure generated content is accurate
3. **Refine**: Adjust prompt based on results
4. **Share**: Document successful prompts for team

### Maintaining Prompt Quality

1. **Version Control**: Track prompt changes in git
2. **Feedback Loop**: Collect team feedback on prompt usefulness
3. **Regular Review**: Update prompts as project evolves
4. **Centralize**: Keep all prompts in `docs/prompts/`

---

## Related Files

- **Backend Prompts**: `docs/prompts/backend-prompts.md`
- **Frontend Prompts**: `docs/prompts/frontend-prompts.md`
- **Project Manager Agent**: `.github/agents/project-manager.agent.md`
- **Documentation Agent**: `.github/agents/documentation.agent.md`
- **Issue Templates**: `.github/ISSUE_TEMPLATE/*.yml`
- **PR Template**: `.github/PULL_REQUEST_TEMPLATE.md`
