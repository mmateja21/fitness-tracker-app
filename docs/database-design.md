# Model baze podataka

Aplikacija koristi relacionu SQLite bazu podataka za čuvanje korisnika,
vežbi, planova treninga i održanih treninga.

## 1. users

Čuva korisnike sistema.

| Kolona | Tip | Ograničenje | Opis |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | Identifikator korisnika |
| full_name | TEXT | NOT NULL | Ime i prezime |
| email | TEXT | NOT NULL, UNIQUE | Email adresa |
| created_at | TEXT | NOT NULL | Datum kreiranja |

## 2. exercises

Čuva dostupne vežbe.

| Kolona | Tip | Ograničenje | Opis |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | Identifikator vežbe |
| name | TEXT | NOT NULL, UNIQUE | Naziv vežbe |
| muscle_group | TEXT | NOT NULL | Primarna mišićna grupa |
| equipment | TEXT | | Potrebna oprema |
| description | TEXT | | Opis i instrukcije |

## 3. workout_plans

Čuva planove treninga korisnika.

| Kolona | Tip | Ograničenje | Opis |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | Identifikator plana |
| user_id | INTEGER | NOT NULL, FOREIGN KEY | Vlasnik plana |
| name | TEXT | NOT NULL | Naziv plana |
| description | TEXT | | Opis plana |
| created_at | TEXT | NOT NULL | Datum kreiranja |

## 4. plan_exercises

Povezuje planove i vežbe. Predstavlja vezu više-prema-više.

| Kolona | Tip | Ograničenje | Opis |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | Identifikator stavke |
| plan_id | INTEGER | NOT NULL, FOREIGN KEY | Plan treninga |
| exercise_id | INTEGER | NOT NULL, FOREIGN KEY | Vežba |
| target_sets | INTEGER | NOT NULL | Planirani broj serija |
| target_reps | INTEGER | NOT NULL | Planirani broj ponavljanja |
| target_weight | REAL | NOT NULL | Planirana težina |
| position | INTEGER | NOT NULL | Redosled vežbe u planu |

## 5. workout_sessions

Čuva podatke o održanim treninzima.

| Kolona | Tip | Ograničenje | Opis |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | Identifikator treninga |
| user_id | INTEGER | NOT NULL, FOREIGN KEY | Korisnik |
| plan_id | INTEGER | FOREIGN KEY, može biti NULL | Korišćeni plan |
| workout_date | TEXT | NOT NULL | Datum treninga |
| duration_minutes | INTEGER | NOT NULL | Trajanje u minutima |
| notes | TEXT | | Napomene |

## 6. session_exercises

Čuva rezultate vežbi tokom održanog treninga.

| Kolona | Tip | Ograničenje | Opis |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | Identifikator rezultata |
| session_id | INTEGER | NOT NULL, FOREIGN KEY | Održani trening |
| exercise_id | INTEGER | NOT NULL, FOREIGN KEY | Izvršena vežba |
| completed_sets | INTEGER | NOT NULL | Broj urađenih serija |
| completed_reps | INTEGER | NOT NULL | Broj ponavljanja |
| weight | REAL | NOT NULL | Korišćena težina |

## Relacije

- Jedan korisnik može imati više planova treninga.
- Jedan korisnik može imati više održanih treninga.
- Jedan plan može sadržati više vežbi.
- Jedna vežba može pripadati različitim planovima.
- Jedan održani trening može sadržati više izvršenih vežbi.
- Jedna vežba može biti izvršena tokom različitih treninga.

## Dodatne operacije

Pored CRUD operacija, aplikacija implementira:

1. Izračunavanje ukupnog volumena treninga:
   `completed_sets * completed_reps * weight`.
2. Pronalaženje najveće korišćene težine za izabranu vežbu.
3. Izračunavanje broja treninga i ukupnog volumena po nedelji.