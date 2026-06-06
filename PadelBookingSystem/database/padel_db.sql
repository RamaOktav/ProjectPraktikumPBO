-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 08 Bulan Mei 2026 pada 09.06
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `padel_db`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_bookings`
--

CREATE TABLE `tb_bookings` (
  `id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `court_id` int(11) DEFAULT NULL,
  `booking_date` date DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `total_price` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_courts`
--

CREATE TABLE `tb_courts` (
  `id` int(11) NOT NULL,
  `court_name` varchar(50) NOT NULL,
  `type` varchar(30) DEFAULT NULL,
  `price_per_hour` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_courts`
--

INSERT INTO `tb_courts` (`id`, `court_name`, `type`, `price_per_hour`, `status`) VALUES
(1, 'Court A', 'Indoor', 100000.00, 'Available'),
(2, 'Court B', 'Indoor', 100000.00, 'Available'),
(3, 'Court C', 'Outdoor', 75000.00, 'Available');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_customers`
--

CREATE TABLE `tb_customers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_payments`
--

CREATE TABLE `tb_payments` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `payment_method` varchar(30) DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `tb_bookings`
--
ALTER TABLE `tb_bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `court_id` (`court_id`);

--
-- Indeks untuk tabel `tb_courts`
--
ALTER TABLE `tb_courts`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `tb_customers`
--
ALTER TABLE `tb_customers`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `tb_payments`
--
ALTER TABLE `tb_payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `tb_bookings`
--
ALTER TABLE `tb_bookings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_courts`
--
ALTER TABLE `tb_courts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `tb_customers`
--
ALTER TABLE `tb_customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_payments`
--
ALTER TABLE `tb_payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `tb_bookings`
--
ALTER TABLE `tb_bookings`
  ADD CONSTRAINT `tb_bookings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `tb_customers` (`id`),
  ADD CONSTRAINT `tb_bookings_ibfk_2` FOREIGN KEY (`court_id`) REFERENCES `tb_courts` (`id`);

--
-- Ketidakleluasaan untuk tabel `tb_payments`
--
ALTER TABLE `tb_payments`
  ADD CONSTRAINT `tb_payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `tb_bookings` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
