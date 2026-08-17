PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     full_name TEXT NOT NULL,
                                     email TEXT NOT NULL UNIQUE,
                                     created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exercises (
                                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                                         name TEXT NOT NULL UNIQUE,
                                         muscle_group TEXT NOT NULL,
                                         equipment TEXT,
                                         description TEXT
);

CREATE TABLE IF NOT EXISTS workout_plans (
                                             id INTEGER PRIMARY KEY AUTOINCREMENT,
                                             user_id INTEGER NOT NULL,
                                             name TEXT NOT NULL,
                                             description TEXT,
                                             created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS plan_exercises (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              plan_id INTEGER NOT NULL,
                                              exercise_id INTEGER NOT NULL,
                                              target_sets INTEGER NOT NULL CHECK (target_sets > 0),
    target_reps INTEGER NOT NULL CHECK (target_reps > 0),
    target_weight REAL NOT NULL CHECK (target_weight >= 0),
    position INTEGER NOT NULL CHECK (position > 0),
    FOREIGN KEY (plan_id) REFERENCES workout_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE RESTRICT,
    UNIQUE (plan_id, exercise_id)
    );

CREATE TABLE IF NOT EXISTS workout_sessions (
                                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                user_id INTEGER NOT NULL,
                                                plan_id INTEGER,
                                                workout_date TEXT NOT NULL,
                                                duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES workout_plans(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS session_exercises (
                                                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                 session_id INTEGER NOT NULL,
                                                 exercise_id INTEGER NOT NULL,
                                                 completed_sets INTEGER NOT NULL CHECK (completed_sets > 0),
    completed_reps INTEGER NOT NULL CHECK (completed_reps > 0),
    weight REAL NOT NULL CHECK (weight >= 0),
    FOREIGN KEY (session_id) REFERENCES workout_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE RESTRICT,
    UNIQUE (session_id, exercise_id)
    );

CREATE INDEX IF NOT EXISTS idx_workout_plans_user_id
    ON workout_plans(user_id);

CREATE INDEX IF NOT EXISTS idx_workout_sessions_user_id
    ON workout_sessions(user_id);

CREATE INDEX IF NOT EXISTS idx_workout_sessions_date
    ON workout_sessions(workout_date);

CREATE INDEX IF NOT EXISTS idx_plan_exercises_plan_id
    ON plan_exercises(plan_id);

CREATE INDEX IF NOT EXISTS idx_session_exercises_session_id
    ON session_exercises(session_id);