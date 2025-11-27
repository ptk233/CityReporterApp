package com.example.cityreporter.config

import com.example.cityreporter.entity.*
import com.example.cityreporter.repository.ReportRepository
import com.example.cityreporter.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@Profile("dev") // Tylko w profilu dev
class DataInitializer {
    
    @Bean
    fun init(
        userRepository: UserRepository,
        reportRepository: ReportRepository,
        passwordEncoder: PasswordEncoder
    ) = CommandLineRunner {
        
        // Tworzenie przykładowych użytkowników
        if (userRepository.count() == 0L) {
            val admin = User(
                email = "admin@cityreporter.pl",
                password = passwordEncoder.encode("Admin123!"),
                name = "Administrator",
                role = UserRole.ADMIN,
                phoneNumber = "+48123456789"
            )
            userRepository.save(admin)
            
            val moderator = User(
                email = "moderator@cityreporter.pl",
                password = passwordEncoder.encode("Moderator123!"),
                name = "Moderator",
                role = UserRole.MODERATOR,
                phoneNumber = "+48987654321"
            )
            userRepository.save(moderator)
            
            val citizen1 = User(
                email = "jan.kowalski@example.pl",
                password = passwordEncoder.encode("User123!"),
                name = "Jan Kowalski",
                role = UserRole.CITIZEN,
                phoneNumber = "+48111222333"
            )
            userRepository.save(citizen1)
            
            val citizen2 = User(
                email = "anna.nowak@example.pl",
                password = passwordEncoder.encode("User123!"),
                name = "Anna Nowak",
                role = UserRole.CITIZEN,
                phoneNumber = "+48444555666"
            )
            userRepository.save(citizen2)
            
            println("✅ Utworzono przykładowych użytkowników:")
            println("   - admin@cityreporter.pl / Admin123!")
            println("   - moderator@cityreporter.pl / Moderator123!")
            println("   - jan.kowalski@example.pl / User123!")
            println("   - anna.nowak@example.pl / User123!")
            
            // Tworzenie przykładowych zgłoszeń
            if (reportRepository.count() == 0L) {
                val report1 = Report(
                    user = citizen1,
                    title = "Dziura w jezdni na ul. Głównej",
                    description = "Duża dziura w asfalcie, zagraża bezpieczeństwu kierowców",
                    category = ReportCategory.ROAD_DAMAGE,
                    status = ReportStatus.NEW,
                    priority = Priority.HIGH,
                    latitude = 51.1078852,
                    longitude = 17.0385376,
                    address = "ul. Główna 45, Wrocław"
                )
                reportRepository.save(report1)
                
                val report2 = Report(
                    user = citizen2,
                    title = "Niedziałająca latarnia",
                    description = "Latarnia uliczna nie świeci od tygodnia",
                    category = ReportCategory.LIGHTING,
                    status = ReportStatus.IN_PROGRESS,
                    priority = Priority.NORMAL,
                    latitude = 51.1100000,
                    longitude = 17.0300000,
                    address = "ul. Parkowa 12, Wrocław"
                )
                reportRepository.save(report2)
                
                val report3 = Report(
                    user = citizen1,
                    title = "Zniszczona ławka w parku",
                    description = "Ławka została zniszczona przez wandali",
                    category = ReportCategory.VANDALISM,
                    status = ReportStatus.RESOLVED,
                    priority = Priority.LOW,
                    latitude = 51.1090000,
                    longitude = 17.0320000,
                    address = "Park Miejski, Wrocław"
                )
                reportRepository.save(report3)
                
                println("✅ Utworzono przykładowe zgłoszenia")
            }
        }
    }
}
