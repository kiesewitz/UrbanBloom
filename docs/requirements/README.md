# Requirements Documentation

This directory contains all requirements documentation for the Digital School Library project.

## Structure

### User Stories
- **`user-stories/`** - Refined user stories for all features
  - 7 Must-Have user stories completed (MVP)
  - Each story includes: refinement discussion, tasks, acceptance criteria, technical notes, DoD

### Transcripts
- **`transscripts/`** - Interview and workshop transcripts
  - Interview transcripts with key stakeholders (e.g., Librarian)
  - Workshop summaries documenting requirement gathering

### Story Map
- **`story-map.md`** - Story map with workflow phases and prioritized features

## Current Status

### Completed ✅
- [x] 7 Refined Must-Have User Stories for MVP
  - US-USR-01: User Authentication
  - US-USR-02: Self-Registration
  - US-CAT-01: Book Catalog Browsing
  - US-CAT-02: Book Search
  - US-CAT-03: Book Details View
  - US-LEN-01: Book Borrowing Request
  - US-LEN-02: My Borrowed Books
- [x] Interview transcripts with stakeholders (e.g. Librarian)
- [x] Workshop summaries
- [x] Story map with workflow phases
- [x] Prototype links integrated in refined user stories
- [x] Update `docs/requirements/user-stories/README.md` with the naming convention and mapping examples

### Pending 📋
- [ ] Should-Have and Could-Have user stories
- [ ] Librarian user stories (book management, returns, etc.)
- [ ] Admin user stories (user management, system configuration)
 

## Requirements Engineering Process

This documentation follows a structured requirements engineering approach:

1. **Stakeholder Identification** - Identify all affected parties
2. **Information Gathering** - Interviews and workshops
3. **Feature Analysis** - Derive features from stakeholder needs
4. **Prioritization** - Dot-voting and MoSCoW method
5. **Epic Creation** - Group related features
6. **Story Mapping** - Organize by workflow phases
7. **User Story Refinement** - Detailed acceptance criteria and technical design

## How to Use This Documentation

### For Developers
- Read the relevant user story before implementing
- Follow the technical architecture outlined in each story
- Implement tasks in the order specified
- Ensure all acceptance criteria are met
- Complete DoD checklist before marking as done

### For Product Owners
- Review acceptance criteria to understand what will be delivered
- Use DoD to verify completion
- Reference dependencies when planning sprints

### For QA/Testers
- Use acceptance criteria as test scenarios
- Verify both functional and non-functional requirements
- Check technical criteria for API contracts

## Related Documentation
- Technical Architecture: `docs/architecture/`
- Development Setup: Root `README.md`
- API Documentation: Generated via OpenAPI/Swagger
