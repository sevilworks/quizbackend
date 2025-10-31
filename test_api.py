import requests
import sys
from colorama import Fore, Style, init
init(autoreset=True)

BASE_URL = "http://localhost:8080/api"

# Utility functions

def print_success(msg):
    print(Fore.GREEN + msg)

def print_error(msg):
    print(Fore.RED + msg)

def print_info(msg):
    print(Fore.CYAN + msg)

def input_choice(prompt, choices):
    print_info(prompt)
    for i, choice in enumerate(choices, 1):
        print(f"{i}. {choice}")
    while True:
        try:
            sel = int(input(Fore.YELLOW + "Select option: "))
            if 1 <= sel <= len(choices):
                return sel
            else:
                print_error("Invalid selection. Try again.")
        except ValueError:
            print_error("Please enter a number.")

# Authentication

def login(role):
    print_info(f"--- {role} Login ---")
    username = input("Username: ")
    password = input("Password: ")
    resp = requests.post(f"{BASE_URL}/auth/login", json={"username": username, "password": password})
    if resp.ok:
        data = resp.json()
        print_success(f"Login successful! Token: {data.get('token')}")
        return data.get('token'), data.get('user')
    else:
        print_error(f"Login failed: {resp.text}")
        return None, None

# Professor actions

def professor_menu(token):
    while True:
        sel = input_choice("Professor Actions:", [
            "Add Quiz",
            "List My Quizzes",
            "Add Question to Quiz",
            "Add Response to Question",
            "Edit Quiz",
            "Delete Quiz",
            "Get Quiz Details",
            "Logout",
        ])
        if sel == 1:
            add_quiz(token)
        elif sel == 2:
            list_quizzes(token)
        elif sel == 3:
            add_question(token)
        elif sel == 4:
            add_response(token)
        elif sel == 5:
            edit_quiz(token)
        elif sel == 6:
            delete_quiz(token)
        elif sel == 7:
            get_quiz_details(token)
        elif sel == 8:
            print_info("Logging out...")
            break

# Student actions

def list_student_participations(token):
    print_info("--- My Quiz Participations ---")
    resp = requests.get(f"{BASE_URL}/quiz/my-participations", 
                       headers={"Authorization": f"Bearer {token}"})
    if resp.ok:
        participations = resp.json()
        if not participations:
            print_info("You haven't participated in any quizzes yet.")
            return
        
        for p in participations:
            quiz = p.get('quiz', {})
            print_success(
                f"Quiz: {quiz.get('title', 'N/A')}\n"
                f"  ID: {quiz.get('id')}\n"
                f"  Score: {p.get('score', 'N/A')}%\n"
                f"  Taken on: {p.get('created_at', 'N/A')}"
            )
    else:
        print_error(f"Failed to list participations: {resp.text}")

def student_menu(token):
    while True:
        sel = input_choice("Student Actions:", [
            "My Quiz Participations",
            "Participate in Quiz by Code",
            "Answer Quiz",
            "Logout",
        ])
        if sel == 1:
            list_student_participations(token)
        elif sel == 2:
            participate_quiz(token)
        elif sel == 3:
            answer_quiz(token)
        elif sel == 4:
            print_info("Logging out...")
            break

# Professor features

def add_quiz(token):
    print_info("--- Add Quiz ---")
    title = input("Quiz Title: ")
    description = input("Description: ")
    duration = input("Duration (minutes): ")
    resp = requests.post(f"{BASE_URL}/quiz/create", headers={"Authorization": f"Bearer {token}"}, json={
        "title": title,
        "description": description,
        "duration": int(duration)
    })
    if resp.ok:
        print_success(f"Quiz created: {resp.json()}")
    else:
        print_error(f"Failed to create quiz: {resp.text}")

def list_quizzes(token, student=False):
    print_info("--- List Quizzes ---")
    # Professor endpoint to list their quizzes
    url = f"{BASE_URL}/quiz/my-quizzes" if not student else f"{BASE_URL}/quiz/all"
    resp = requests.get(url, headers={"Authorization": f"Bearer {token}"})
    if resp.ok:
        quizzes = resp.json()
        for q in quizzes:
            print_success(f"ID: {q.get('id')}, Title: {q.get('title')}, Code: {q.get('code')}")
    else:
        print_error(f"Failed to list quizzes: {resp.text}")

def add_question(token):
    print_info("--- Add Question ---")
    quiz_id = input("Quiz ID: ")
    question_text = input("Question Text: ")
    resp = requests.post(f"{BASE_URL}/quiz/{quiz_id}/questions", headers={"Authorization": f"Bearer {token}"}, json={
        "questionText": question_text
    })
    if resp.ok:
        print_success(f"Question added: {resp.json()}")
    else:
        print_error(f"Failed to add question: {resp.text}")

def add_response(token):
    print_info("--- Add Response ---")
    question_id = input("Question ID: ")
    response_text = input("Response Text: ")
    is_correct = input("Is Correct? (y/n): ").lower() == 'y'
    # The API expects snake_case for the response text field (`response_text`) and camelCase for isCorrect
    resp = requests.post(f"{BASE_URL}/quiz/questions/{question_id}/responses", headers={"Authorization": f"Bearer {token}"}, json={
        "response_text": response_text,
        "isCorrect": is_correct
    })
    if resp.ok:
        print_success(f"Response added: {resp.json()}")
    else:
        print_error(f"Failed to add response: {resp.text}")

def edit_quiz(token):
    print_info("--- Edit Quiz ---")
    quiz_id = input("Quiz ID: ")
    title = input("New Title: ")
    description = input("New Description: ")
    duration = input("New Duration (minutes): ")
    resp = requests.put(f"{BASE_URL}/quiz/{quiz_id}", headers={"Authorization": f"Bearer {token}"}, json={
        "title": title,
        "description": description,
        "duration": int(duration)
    })
    if resp.ok:
        print_success(f"Quiz updated: {resp.json()}")
    else:
        print_error(f"Failed to update quiz: {resp.text}")

def delete_quiz(token):
    print_info("--- Delete Quiz ---")
    quiz_id = input("Quiz ID: ")
    resp = requests.delete(f"{BASE_URL}/quiz/{quiz_id}", headers={"Authorization": f"Bearer {token}"})
    if resp.ok:
        print_success("Quiz deleted successfully.")
    else:
        print_error(f"Failed to delete quiz: {resp.text}")

def get_quiz_details(token):
    print_info("--- Get Quiz Details ---")
    quiz_id = input("Enter Quiz ID: ")
    resp = requests.get(f"{BASE_URL}/quiz/{quiz_id}", headers={"Authorization": f"Bearer {token}"})
    if resp.ok:
        print_success(f"Quiz Details: {resp.text}")
    else:
        print_error(f"Failed to get quiz details: {resp.text}")

# Student features

def participate_quiz(token):
    print_info("--- Participate in Quiz ---")
    code = input("Quiz Code: ")
    # Register participation by posting to /quiz/join/{code}
    resp = requests.post(f"{BASE_URL}/quiz/join/{code}", headers={"Authorization": f"Bearer {token}"})
    if resp.ok:
        participation = resp.json()
        # Try to read nested quiz information if present
        quiz = participation.get('quiz') if isinstance(participation, dict) else None
        if quiz:
            print_success(f"Participation created for quiz: Title: {quiz.get('title')}, ID: {quiz.get('id')}")
        else:
            print_success(f"Participation created: {participation}")
        print_info("You can now submit answers using the quiz ID.")
    else:
        print_error(f"Failed to participate: {resp.text}")

def answer_quiz(token):
    print_info("--- Answer Quiz ---")
    quiz_id = input("Quiz ID: ")
    print_info("Enter response IDs (comma-separated) for your answers:")
    response_ids = [int(x.strip()) for x in input("Response IDs: ").split(",")]
    
    resp = requests.post(f"{BASE_URL}/quiz/{quiz_id}/submit", headers={"Authorization": f"Bearer {token}"}, json={
        "selectedResponseIds": response_ids,
        "guestId": None  # Only needed for guest participation
    })
    if resp.ok:
        participation = resp.json()
        print_success(f"Quiz submitted successfully! Score: {participation.get('score', 'N/A')}")
    else:
        print_error(f"Failed to submit quiz: {resp.text}")

# Professor test suite

def professor_test_suite():
    print_info("--- Professor Test Suite ---")
    # 1. Register Professor
    print_info("Registering Professor...")
    reg_data = {
        "username": "prof_test",
        "email": "prof_test@test.com",
        "password": "testpass123",
        "firstName": "Test",
        "lastName": "Prof"
    }
    reg_resp = requests.post(f"{BASE_URL}/auth/register/professor", json=reg_data)
    print_success(f"Register Response: {reg_resp.text}")

    # 2. Login
    print_info("Logging in as Professor...")
    login_resp = requests.post(f"{BASE_URL}/auth/login", json={"username": reg_data["username"], "password": reg_data["password"]})
    if not login_resp.ok:
        print_error(f"Login failed: {login_resp.text}")
        return
    token = login_resp.json().get("token")
    print_success(f"Login Token: {token}")

    # 3. Create Quiz
    print_info("Creating Quiz...")
    quiz_data = {
        "title": "API Test Quiz",
        "description": "Quiz for API testing",
        "duration": 15
    }
    quiz_resp = requests.post(f"{BASE_URL}/quiz/create", headers={"Authorization": f"Bearer {token}"}, json=quiz_data)
    print_success(f"Create Quiz Response: {quiz_resp.text}")
    quiz_id = quiz_resp.json().get("id")

    # 4. List My Quizzes
    print_info("Listing My Quizzes...")
    list_resp = requests.get(f"{BASE_URL}/quiz/my-quizzes", headers={"Authorization": f"Bearer {token}"})
    print_success(f"My Quizzes: {list_resp.text}")

    # 5. Update Quiz
    print_info("Updating Quiz...")
    update_data = {
        "title": "API Test Quiz Updated",
        "description": "Updated quiz description",
        "duration": 20
    }
    update_resp = requests.put(f"{BASE_URL}/quiz/{quiz_id}", headers={"Authorization": f"Bearer {token}"}, json=update_data)
    print_success(f"Update Quiz Response: {update_resp.text}")

    # 6. Add Question
    print_info("Adding Question...")
    question_data = {"questionText": "What is 2+2?"}
    question_resp = requests.post(f"{BASE_URL}/quiz/{quiz_id}/questions", headers={"Authorization": f"Bearer {token}"}, json=question_data)
    print_success(f"Add Question Response: {question_resp.text}")
    question_id = question_resp.json().get("id")

    # 7. Add Response to Question
    print_info("Adding Response to Question...")
    response_data = {"responseText": "4", "isCorrect": True}
    response_resp = requests.post(f"{BASE_URL}/quiz/questions/{question_id}/responses", headers={"Authorization": f"Bearer {token}"}, json=response_data)
    print_success(f"Add Response Response: {response_resp.text}")

    # 8. View Quiz Participations
    print_info("Viewing Quiz Participations...")
    part_resp = requests.get(f"{BASE_URL}/quiz/{quiz_id}/participations", headers={"Authorization": f"Bearer {token}"})
    print_success(f"Quiz Participations: {part_resp.text}")

    # 9. Delete Quiz
    print_info("Deleting Quiz...")
    del_resp = requests.delete(f"{BASE_URL}/quiz/{quiz_id}", headers={"Authorization": f"Bearer {token}"})
    print_success(f"Delete Quiz Response: {del_resp.text}")

    # 10. Get Quiz Details
    print_info("Getting Quiz Details...")
    details_resp = requests.get(f"{BASE_URL}/quiz/{quiz_id}", headers={"Authorization": f"Bearer {token}"})
    print_success(f"Quiz Details: {details_resp.text}")

# Main menu

def main():
    print(Fore.MAGENTA + Style.BRIGHT + "\n=== Quiz Platform Interactive Test ===\n")
    while True:
        sel = input_choice("Select role:", ["Professor", "Student", "Professor Test Suite", "Exit"])
        if sel == 1:
            token, user = login("Professor")
            if token:
                professor_menu(token)
        elif sel == 2:
            token, user = login("Student")
            if token:
                student_menu(token)
        elif sel == 3:
            professor_test_suite()
        elif sel == 4:
            print_info("Exiting...")
            sys.exit(0)

if __name__ == "__main__":
    main()
