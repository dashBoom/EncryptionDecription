package fm.mrc.encryptiondecription

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fm.mrc.encryptiondecription.ui.theme.EncryptionDecriptionTheme
import fm.mrc.encryptiondecription.utils.EncryptionUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EncryptionDecriptionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EncryptionScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptionScreen() {
    var isEncrypting by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    var keyText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var cipherText by remember { mutableStateOf("") }
    var keyString by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Mode: ")
            Switch(
                checked = isEncrypting,
                onCheckedChange = { 
                    isEncrypting = it
                    inputText = ""
                    keyText = ""
                    resultText = ""
                    cipherText = ""
                    keyString = ""
                }
            )
            Text(if (isEncrypting) "Encryption" else "Decryption")
        }

        // Input Field
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text(if (isEncrypting) "Enter Plaintext" else "Enter Ciphertext") },
            modifier = Modifier.fillMaxWidth()
        )

        // Key Field (only for decryption)
        if (!isEncrypting) {
            OutlinedTextField(
                value = keyText,
                onValueChange = { keyText = it },
                label = { Text("Enter Decryption Key") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Process Button
        Button(
            onClick = {
                try {
                    if (isEncrypting) {
                        val key = EncryptionUtils.generateKey()
                        cipherText = EncryptionUtils.encrypt(inputText, key)
                        keyString = EncryptionUtils.keyToString(key)
                        resultText = ""  // Clear combined result
                    } else {
                        if (keyText.isBlank()) {
                            Toast.makeText(context, "Please enter a key", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val plaintext = EncryptionUtils.decrypt(inputText, keyText)
                        resultText = "Plaintext: $plaintext"
                        cipherText = ""
                        keyString = ""
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        ) {
            Text(if (isEncrypting) "Encrypt" else "Decrypt")
        }

        // Results Section
        if (isEncrypting && cipherText.isNotEmpty()) {
            // Show Ciphertext with copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ciphertext: $cipherText",
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ciphertext", cipherText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Ciphertext copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Ciphertext"
                    )
                }
            }

            // Show Key with copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Key: $keyString",
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Key", keyString)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Key copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Key"
                    )
                }
            }
        } else if (!isEncrypting && resultText.isNotEmpty()) {
            // Show decryption result with copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resultText,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Decryption Result", resultText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy"
                    )
                }
            }
        }
    }
}