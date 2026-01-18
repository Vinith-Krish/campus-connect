# CampusConnect 🎓

A modern event management platform connecting students and clubs across college campuses. Discover, create, and participate in campus events seamlessly.

![React](https://img.shields.io/badge/React-18-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-3.x-38bdf8)

## ✨ Features

### For Students
- **🔍 Discover Events** - Browse events from colleges across your city with multi-field search
- **🎯 Smart Filtering** - Filter by category (Tech, Cultural, Sports, Workshop) and search by event name, college, or venue
- **📝 Easy Registration** - Sign up for events with a single click
- **❤️ Save for Later** - Mark events as interested to track them on your profile
- **👤 Personal Dashboard** - View all registered and interested events in one place
- **✏️ Profile Management** - Edit your profile information

### For Club Admins
- **➕ Create Events** - Publish events for your college community with rich details
- **📊 Event Management** - Full control over your events
- **📈 Track Engagement** - Monitor registration counts and user interest
- **🏫 Auto-Association** - Events automatically linked to your college

### System Features
- **🔒 Secure Authentication** - JWT-based auth with role-based access control
- **⚡ Real-time Search** - Debounced search for optimal performance
- **📱 Fully Responsive** - Beautiful UI on desktop, tablet, and mobile
- **🎨 Modern Design** - Clean, gradient-rich interface with smooth animations
- **🔄 Auto-Refresh** - Profile updates reflected immediately

## 🛠️ Tech Stack

### Frontend
- **React 18.3** - Modern UI library
- **Vite** - Lightning-fast build tool
- **React Router v6** - Client-side routing
- **TailwindCSS** - Utility-first CSS framework
- **shadcn/ui** - High-quality component library
- **Lucide React** - Beautiful icon set
- **Axios** - Promise-based HTTP client

### Backend
- **Spring Boot 3.x** - Enterprise Java framework
- **Spring Security** - Authentication & authorization
- **JWT** - Stateless token-based auth
- **Hibernate/JPA** - ORM for database operations
- **PostgreSQL** - Robust relational database
- **Maven** - Dependency management

## 📋 Prerequisites

Before running this project, ensure you have:

- **Node.js** 16.x or higher
- **npm** or **bun** package manager
- **Java** 17 or higher
- **Maven** 3.6+
- **PostgreSQL** 12+

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/campus-connect.git
cd campus-connect
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE campusconnect;
```

### 3. Backend Setup

```bash
# Navigate to backend directory
cd campus-connect-backend

# Configure application.properties
# Edit: src/main/resources/application.properties
```

**application.properties:**
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/campusconnect
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=your_super_secret_key_min_256_bits
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

**Run the backend:**
```bash
mvn clean install
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`

### 4. Frontend Setup

```bash
# From project root
cd campus-connect

# Install dependencies
npm install
# or
bun install

# Start development server
npm run dev
# or
bun run dev
```

Frontend runs at `http://localhost:5173`

## 📁 Project Structure

```
campus-connect/
├── src/
│   ├── components/           # Reusable components
│   │   ├── ui/              # shadcn/ui components
│   │   ├── CategoryFilter.jsx
│   │   ├── EventCard.jsx
│   │   ├── Navbar.jsx
│   │   ├── ProtectedRoute.jsx
│   │   └── SearchBar.jsx
│   ├── context/
│   │   └── AuthContext.jsx  # Global auth state
│   ├── hooks/               # Custom React hooks
│   ├── lib/
│   │   └── utils.js         # Utility functions
│   ├── pages/               # Route pages
│   │   ├── CreateEvent.jsx
│   │   ├── EventDetails.jsx
│   │   ├── Events.jsx
│   │   ├── ForgotPassword.jsx
│   │   ├── Landing.jsx
│   │   ├── Login.jsx
│   │   ├── Profile.jsx
│   │   ├── Register.jsx
│   │   └── ResetPassword.jsx
│   └── services/            # API layer
│       ├── authService.js
│       ├── axiosInstance.js
│       ├── eventService.js
│       └── userService.js
├── public/                  # Static assets
├── README.md
├── package.json
└── tailwind.config.js
```

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login user | No |
| POST | `/api/auth/forgot-password` | Request password reset | No |
| POST | `/api/auth/reset-password` | Reset password | No |

### Events
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/events` | Get all events (search & filter) | No |
| GET | `/api/events/:id` | Get event details | No |
| POST | `/api/events` | Create event | Admin |
| PUT | `/api/events/:id` | Update event | Admin |
| DELETE | `/api/events/:id` | Delete event | Admin |
| POST | `/api/events/:id/register` | Register for event | Yes |
| DELETE | `/api/events/:id/unregister` | Unregister from event | Yes |
| POST | `/api/events/:id/interested` | Mark as interested | Yes |

### Users
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/users/me` | Get user profile | Yes |
| PUT | `/api/users/me` | Update profile | Yes |
| GET | `/api/users/me/events` | Get user's events | Yes |

## 🎨 Key Features Implementation

### Multi-Field Search
Backend searches across:
- Event title
- College name
- Venue
- Description

```java
@Query("SELECT e FROM Event e WHERE " +
       "LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
       "LOWER(e.collegeName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
       "LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%'))")
List<Event> searchEvents(@Param("search") String search);
```

### Debounced Search
Frontend implements 300ms debounce to reduce API calls:
```javascript
useEffect(() => {
  const timer = setTimeout(() => {
    if (localValue !== value) {
      onChange(localValue);
    }
  }, 300);
  return () => clearTimeout(timer);
}, [localValue]);
```

### Category Filtering
Categories automatically converted to uppercase for backend consistency:
```javascript
if (category) params.append('category', category.toUpperCase());
```

## 🔐 Security Features

- **JWT Authentication** - Stateless token-based auth
- **Password Hashing** - BCrypt encryption
- **Protected Routes** - Frontend route guards
- **Role-Based Access** - STUDENT vs CLUB_ADMIN permissions
- **CORS Configuration** - Controlled cross-origin requests
- **SQL Injection Prevention** - Parameterized queries
- **Auto Logout** - 401 responses trigger logout

## 🎯 User Roles

### STUDENT
- Browse and search events
- Register for events
- Mark events as interested
- View and edit personal profile
- Cannot create events

### CLUB_ADMIN
- All student permissions
- Create new events
- Edit own events
- Delete own events
- Events auto-associated with their college

## 📱 Responsive Breakpoints

- **Mobile**: 320px - 767px
- **Tablet**: 768px - 1023px
- **Desktop**: 1024px+
- **Large Desktop**: 1440px+

## 🎨 Design System

### Colors
- **Primary**: Purple gradient (#8B5CF6 to #6366F1)
- **Accent**: Orange gradient (#F97316 to #FB923C)
- **Categories**:
  - Tech: Blue
  - Cultural: Rose
  - Sports: Green
  - Workshop: Amber

### Animations
- Slide-up on page load
- Fade-in for cards
- Smooth transitions
- Hover effects

## 🧪 Sample Data

Use the provided sample events JSON for testing:
- Workshop from PES University
- Tech event from Dayananda Sagar College

See `sample-events.json` for Postman import.

## 🐛 Troubleshooting

### Frontend won't start
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Backend connection failed
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure port 8080 is available

### CORS errors
- Verify backend CORS configuration allows `http://localhost:5173`
- Check `axiosInstance.js` baseURL matches backend URL

### Registration fails with "collegename" error
- Ensure database migration ran successfully
- Check User entity has `collegename` column
- Verify frontend sends `collegename` (lowercase)

## 🚀 Deployment

### Frontend (Vercel/Netlify)
```bash
npm run build
# Deploy dist/ folder
```

### Backend (Railway/Heroku)
```bash
mvn clean package
# Deploy generated JAR file
```

### Database
- Use managed PostgreSQL (AWS RDS, DigitalOcean, Railway)
- Update connection string in production

## 🔄 Future Enhancements

- [ ] Email notifications for event updates
- [ ] Event calendar/timeline view
- [ ] QR code-based event check-in
- [ ] Event review and rating system
- [ ] Image uploads for events
- [ ] Admin analytics dashboard
- [ ] Event recommendations based on interests
- [ ] Social media integration
- [ ] Event reminders via email/push notifications
- [ ] Multi-language support

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Authors

- **M Vinith Krishna**

## 🙏 Acknowledgments

- [shadcn/ui](https://ui.shadcn.com/) - Component library
- [Lucide Icons](https://lucide.dev/) - Icon set
- [TailwindCSS](https://tailwindcss.com/) - CSS framework
- Spring Boot and React communities

## 📞 Support

For issues or questions:
- Open an issue on GitHub
- Email: support@campusconnect.com

---

**Made with ❤️ for college communities**
