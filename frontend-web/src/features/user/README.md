# Feature: User

**Context:** Generic Domain - Benutzerprofil, Einstellungen

---

## Structure

```
user/
├── data/
│   ├── models/
│   │   └── UserDTO.ts          # User-Profil
│   └── repositories/
│       └── UserRepository.ts
│
└── presentation/
    ├── atoms/
    │   └── UserAvatar.tsx
    ├── molecules/
    │   └── ProfileHeader.tsx   # Avatar + Name + E-Mail
    ├── organisms/
    │   └── ProfileCard.tsx     # Profil mit Stats
    └── pages/
        └── ProfilePage.tsx     # Profil-Seite
```

---

## Data Models

```typescript
// models/UserDTO.ts
export interface UserDTO {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  userGroup: 'STUDENT' | 'TEACHER' | 'LIBRARIAN';
  borrowingLimit: number;
  activeLoansCount?: number;
  overdueLoansCount?: number;
}
```

---

## Repository

```typescript
// repositories/UserRepository.ts
export class UserRepository {
  async getProfile(): Promise<UserDTO>;
  async updateProfile(data: Partial<UserDTO>): Promise<UserDTO>;
}
```

---

## Components

**Atoms:** `UserAvatar` - User Avatar  
**Molecules:** `ProfileHeader` - Avatar + Name  
**Organisms:** `ProfileCard` - Profil mit Statistiken  
**Pages:** `ProfilePage`

---

## References

- [Backend: User Context](../../../../backend/module-user/README.md)
