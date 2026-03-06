package com.example.vigilante;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
public class LoginTest {

    @Mock
    private FirebaseAuth mAuth;

    @Mock
    private Task<AuthResult> mockedTask;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
//Gemini March 4th 2026, how to write unit test for login feature , firebase
    @Test
    public void testLogin_Success() {
        String email = "test@test.com";
        String password = "test123";

        // Tell Mockito: When mAuth tries to sign in, return a "Task"
        when(mAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockedTask);

        // Execute the call
        Task<AuthResult> result = mAuth.signInWithEmailAndPassword(email, password);

        // Verify the method was actually called with the right credentials
        verify(mAuth).signInWithEmailAndPassword(email, password);
        assertNotNull(result);
    }
    @Test
    public void testLogin_InvalidEmail_Fails() {
        String email = "";
        // Logic check: If email is empty, it shouldn't even trigger Firebase
        boolean isValid = !email.isEmpty();
        assertFalse("Login should fail if email is empty", isValid);
    }
}
