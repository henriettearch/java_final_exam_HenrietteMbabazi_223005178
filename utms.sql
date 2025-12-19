
--
-- Database: `utms`
--

-- --------------------------------------------------------

--
-- Table structure for table `bills`
--

DROP TABLE IF EXISTS `bills`;
CREATE TABLE IF NOT EXISTS `bills` (
  `bill_id` int NOT NULL AUTO_INCREMENT,
  `consumption_id` int NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` enum('Pending','Paid') DEFAULT 'Pending',
  `issue_date` date NOT NULL,
  `due_date` date NOT NULL,
  PRIMARY KEY (`bill_id`),
  KEY `consumption_id` (`consumption_id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `bills`
--

INSERT INTO `bills` (`bill_id`, `consumption_id`, `total_amount`, `status`, `issue_date`, `due_date`) VALUES
(1, 1, 102600.00, 'Pending', '2025-09-30', '2025-10-15'),
(2, 2, 9840.00, 'Paid', '2025-09-30', '2025-10-15'),
(3, 3, 4000.00, 'Paid', '2025-09-30', '2025-10-15'),
(4, 4, 132900.00, 'Pending', '2025-09-30', '2025-10-15'),
(5, 5, 12000.00, 'Paid', '2025-09-30', '2025-10-15'),
(6, 6, 5000.00, 'Pending', '2025-09-30', '2025-10-15'),
(7, 7, 118080.00, 'Paid', '2025-09-30', '2025-10-15'),
(8, 8, 8640.00, 'Paid', '2025-09-30', '2025-10-15'),
(9, 9, 3750.00, 'Pending', '2025-09-30', '2025-10-15'),
(10, 9, 32000.00, 'Pending', '2025-11-03', '2025-12-03'),
(11, 11, 36000.00, 'Paid', '2025-11-11', '2025-12-11');

-- --------------------------------------------------------

--
-- Table structure for table `complaints`
--

DROP TABLE IF EXISTS `complaints`;
CREATE TABLE IF NOT EXISTS `complaints` (
  `complaint_id` int NOT NULL AUTO_INCREMENT,
  `subscriber_id` int NOT NULL,
  `utility_id` int NOT NULL,
  `subject` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `status` enum('Pending','In Progress','Resolved','Rejected') DEFAULT 'Pending',
  `submitted_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `resolved_by` int DEFAULT NULL,
  `resolution_note` text,
  PRIMARY KEY (`complaint_id`),
  KEY `subscriber_id` (`subscriber_id`),
  KEY `utility_id` (`utility_id`),
  KEY `resolved_by` (`resolved_by`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `complaints`
--

INSERT INTO `complaints` (`complaint_id`, `subscriber_id`, `utility_id`, `subject`, `description`, `status`, `submitted_date`, `resolved_by`, `resolution_note`) VALUES
(1, 4, 1, 'High Electricity Bill', 'My electricity bill this month seems much higher than usual. Please verify the meter reading.', 'Resolved', '2025-10-28 22:56:03', 2, 'Meter reading was correct; advised subscriber to unplug idle appliances.'),
(2, 4, 2, 'Low Water Pressure', 'The water pressure in my area has been very low for three days.', 'In Progress', '2025-10-28 22:56:03', 3, 'Technician scheduled to check the local pipeline.'),
(3, 4, 3, 'Late Waste Collection', 'Waste collection was delayed by two days this week.', 'Resolved', '2025-10-28 22:56:03', 2, 'Waste collection schedule was adjusted for her area.'),
(4, 5, 1, 'Power Outage', 'There was a power outage in my area yesterday night and no one informed us.', 'Resolved', '2025-10-28 22:56:03', 3, 'Outage was caused by transformer maintenance; service restored.'),
(5, 5, 2, 'Incorrect Water Bill', 'My water bill seems incorrect compared to my usual consumption.', 'Pending', '2025-10-28 22:56:03', NULL, NULL),
(6, 6, 3, 'Uncollected Waste', 'Garbage has not been collected for a week. It is causing a bad smell.', 'In Progress', '2025-10-28 22:56:03', 2, 'Staff assigned to follow up with the waste management team.'),
(7, 6, 1, 'Voltage Fluctuation', 'Electricity voltage keeps fluctuating and affecting my home appliances.', 'Resolved', '2025-10-28 22:56:03', 3, 'Voltage stabilizer installed in her area to fix the issue.'),
(8, 11, 3, 'not collected', 'hey, it\'s been a week now, wastes from my home have never been collected, and it is causing us diseases from low sanitation', 'Pending', '2025-11-02 17:29:36', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `consumption`
--

DROP TABLE IF EXISTS `consumption`;
CREATE TABLE IF NOT EXISTS `consumption` (
  `consumption_id` int NOT NULL AUTO_INCREMENT,
  `subscriber_id` int NOT NULL,
  `utility_id` int NOT NULL,
  `month` varchar(20) NOT NULL,
  `units_used` decimal(10,2) NOT NULL,
  `recorded_by` int NOT NULL,
  `date_recorded` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`consumption_id`),
  KEY `subscriber_id` (`subscriber_id`),
  KEY `utility_id` (`utility_id`),
  KEY `recorded_by` (`recorded_by`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `consumption`
--

INSERT INTO `consumption` (`consumption_id`, `subscriber_id`, `utility_id`, `month`, `units_used`, `recorded_by`, `date_recorded`) VALUES
(1, 4, 1, 'September 2025', 85.50, 2, '2025-10-28 21:54:27'),
(2, 4, 2, 'September 2025', 12.30, 2, '2025-10-28 21:54:27'),
(3, 4, 3, 'September 2025', 8.00, 2, '2025-10-28 21:54:27'),
(4, 5, 1, 'September 2025', 110.75, 3, '2025-10-28 21:54:27'),
(5, 5, 2, 'September 2025', 15.00, 3, '2025-10-28 21:54:27'),
(6, 5, 3, 'September 2025', 10.00, 3, '2025-10-28 21:54:27'),
(7, 6, 1, 'September 2025', 98.40, 2, '2025-10-28 21:54:27'),
(8, 6, 2, 'September 2025', 10.80, 2, '2025-10-28 21:54:27'),
(10, 11, 2, 'october', 40.00, 1, '2025-11-03 13:37:52'),
(11, 11, 1, 'september', 30.00, 11, '2025-11-11 13:24:46');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `bill_id` int NOT NULL,
  `subscriber_id` int NOT NULL,
  `amount_paid` decimal(10,2) NOT NULL,
  `payment_method` enum('Mobile Money','Cash','Bank Transfer') NOT NULL,
  `payment_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`payment_id`),
  KEY `bill_id` (`bill_id`),
  KEY `subscriber_id` (`subscriber_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `bill_id`, `subscriber_id`, `amount_paid`, `payment_method`, `payment_date`) VALUES
(1, 2, 4, 9840.00, 'Mobile Money', '2025-10-02 10:30:00'),
(2, 3, 4, 4000.00, 'Cash', '2025-10-03 14:10:00'),
(3, 5, 5, 12000.00, 'Bank Transfer', '2025-10-04 11:25:00'),
(4, 7, 6, 118080.00, 'Mobile Money', '2025-10-05 09:40:00'),
(5, 8, 6, 8640.00, 'Mobile Money', '2025-10-06 08:50:00'),
(6, 9, 12, 15000.00, 'Mobile Money', '2025-11-02 17:57:26'),
(7, 11, 11, 36000.00, 'Mobile Money', '2025-11-11 13:39:04');

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
CREATE TABLE IF NOT EXISTS `reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `report_type` varchar(100) NOT NULL,
  `generated_by` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `report_data` text,
  PRIMARY KEY (`report_id`),
  KEY `generated_by` (`generated_by`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reports`
--

INSERT INTO `reports` (`report_id`, `report_type`, `generated_by`, `created_at`, `report_data`) VALUES
(1, 'Daily Electricity Consumption Report', 2, '2025-10-28 23:00:40', 'Total consumption recorded for all subscribers: 294.65 kWh. \n  Highest consumer: John SANGWA (110.75 kWh). \n  Average usage: 98.21 kWh per subscriber.'),
(2, 'Monthly Billing Summary', 3, '2025-10-28 23:00:40', 'Bills issued for September 2025: 9 total. \n  Paid bills: 5. Pending bills: 4. \n  Total amount billed: 524,810 RWF. Total amount paid: 160,560 RWF.'),
(3, 'Complaints Summary Report', 2, '2025-10-28 23:00:40', 'Total complaints submitted: 7. \n  Resolved: 4, In Progress: 2, Pending: 1. \n  Most frequent complaint type: Electricity-related issues.'),
(4, 'Payments Transaction Report', 3, '2025-10-28 23:00:40', 'Payment methods used: Mobile Money (3), Cash (1), Bank Transfer (1). \n  Total amount received: 160,560 RWF. \n  Staff verified and reconciled all payment receipts.'),
(5, 'Waste Management Activity Report', 2, '2025-10-28 23:00:40', 'Total waste collected this month: 25.5 kg. \n  Complaints about delays: 2. \n  Status: Collection frequency adjusted and routes optimized.');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `Full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('Admin','Staff','Subscriber') NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `Full_name`, `email`, `password`, `role`, `phone`, `address`, `created_at`) VALUES
(1, 'admin01', 'Henriette MBABAZI', 'admin@utms.com', 'admin123', 'Admin', '0788000000', 'Kigali, Rwanda', '2025-10-27 19:38:36'),
(2, 'staff01', 'David KARANGWA', 'staff1@utms.com', 'staff123', 'Staff', '0788111111', 'Huye, Rwanda', '2025-10-27 19:38:36'),
(3, 'staff02', 'Eric TUZA', 'staff2@utms.com', 'staff234', 'Staff', '0788111222', 'Rubavu, Rwanda', '2025-10-27 19:38:36'),
(4, 'aliceN', 'Alice UMUHOZA', 'alice@gmail.com', 'alice23', 'Subscriber', '0788222222', 'Musanze, Rwanda', '2025-10-27 19:38:36'),
(5, 'johnS', 'John SANGWA', 'john@gmail.com', 'john13', 'Subscriber', '0788333333', 'Nyamagabe, Rwanda', '2025-10-27 19:38:36'),
(6, 'mariaK', 'Maria KEZA', 'maria@gmail.com', 'maria12', 'Subscriber', '0788444444', 'Nyanza, Rwanda', '2025-10-27 19:38:36'),
(7, 'henri_m', 'GIRIMBABAZI Hortance', 'hortance@gmail.com', '123123', 'Subscriber', '0798098765', 'Gasabo', '2025-10-30 08:22:31'),
(8, 'neema', 'UWINEMA Costa', 'Costa@gmail.com', 'costa123', 'Subscriber', '0789018275', 'Kicukiro', '2025-10-30 09:27:28'),
(9, 'GIANT', 'GIANT OLIVIER', 'olla@gmail.com', 'olaa12121', 'Subscriber', '0078901234', 'KIGALI/NYARUGENGE', '2025-10-31 12:28:01'),
(10, 'kabebe', 'HIRWA Parfait', 'Parfait@gmail.com', 'hirwa123', 'Subscriber', '0789126458', 'Bugesera', '2025-11-02 15:07:57'),
(11, 'bebe', 'ISHIMWE Jessica', 'jessica@gmail.com', 'jeje1223', 'Subscriber', '0790172190', 'Gasabo', '2025-11-02 15:11:14'),
(12, 'jaja', 'Jeannette Keza', 'keza@gmail.com', 'jaja123', 'Staff', '0789012343', 'Kicukiro', '2025-11-11 09:10:44'),
(13, 'emma', 'Emma Claudine', 'emmah@gmail.com', '12345', 'Subscriber', '0790123456', 'Huye', '2025-11-22 16:43:55'),
(14, 'Mr T', 'Tity Ba', 'ba@gmail.com', 'ba123', 'Staff', '0789018213', 'Kicukiro', '2025-11-27 17:09:41');

-- --------------------------------------------------------

--
-- Table structure for table `utility_types`
--

DROP TABLE IF EXISTS `utility_types`;
CREATE TABLE IF NOT EXISTS `utility_types` (
  `utility_id` int NOT NULL AUTO_INCREMENT,
  `utility_name` enum('Electricity','Water','Waste') NOT NULL,
  `rate_per_unit` decimal(10,2) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`utility_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `utility_types`
--

INSERT INTO `utility_types` (`utility_id`, `utility_name`, `rate_per_unit`, `description`, `created_at`) VALUES
(1, 'Electricity', 1200.00, ' Price per kilowat', '2025-10-28 19:41:40'),
(2, 'Water', 800.00, 'price per meter ', '2025-10-28 19:41:40'),
(3, 'Waste', 500.00, ' price per kilogram', '2025-10-28 19:41:40');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
