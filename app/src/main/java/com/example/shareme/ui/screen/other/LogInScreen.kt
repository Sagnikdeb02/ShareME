package com.example.shareme.ui.screen.other

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shareme.AuthState
import com.example.shareme.AuthViewModel
import com.example.shareme.R
import com.example.shareme.core.util.Screen

@Composable
fun LogIn(
    navController: NavController,
    authViewModel: AuthViewModel
){
    var email by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate(Screen.Content.route)
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box{
        Image(painter = painterResource(id = R.drawable.img_1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize())
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Log In",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        Image(painter = painterResource(id = R.drawable.img_2),
            contentDescription = null,
            modifier = Modifier
                .size(110.dp)
                .background(
                    color = Color(0xFFCDA9EA),
                    shape = RoundedCornerShape(50.dp)
                ))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.login(email, password)
            },
            enabled = authState.value != AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .size(width = 280.dp, height = 50.dp),
            colors = ButtonDefaults.buttonColors(Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Log In", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate(Screen.SignUpScreen.route)
            }
        ){
            Text(text = "Already have an account, Sign in")
        }

    }
}
