package com.example.mobilecomputinghw1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputinghw1.ui.theme.MobilecomputingHW1Theme

data class Message(val author: String, val body: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilecomputingHW1Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("second") {
            SecondScreen(navController)
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(SampleData.conversationSample) { message ->
                MessageCard(message)
            }
        }
        Button(
            onClick = {
                navController.navigate("second") {
                    popUpTo("main") { saveState = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Go to Second View")
        }
    }
}

@Composable
fun SecondScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Second View", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("main") {
                popUpTo("main") { inclusive = true }
            }
        }) {
            Text("Go Back")
        }
    }
}

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

object SampleData {
    val conversationSample = listOf(
        Message("Pena", "moro, haluutko kuulla vitsin tai pari"),
        Message("Pena", "Eräs Lada-kuski ajoi moottoritiellä tyytyväisenä, kunnes auto päätti lopettaa yhteistyön. Ferrari-kuski pysähtyi auttamaan ja kysyi: 'Tarvitsetko apua?' Lada-kuski vastasi: 'Ei hätää, meillä on vika-valot… me vaan odotellaan, että tämä muuttuu luksusongelmaksi.'"),
        Message("Pena", "Mies meni baariin kertomaan vitsejä, mutta baarimikko sanoi: 'Täällä kaikki on kuultu.' Mies vastasi: 'Ok, aloitan sitten siitä, mistä viimeksi jäi kertomatta… mutta ensin lasi vettä!'"),
        Message("Pena", "Hoitaja oli innoissaan, kun asiakas kertoi kirjoittaneensa kirjan. Illalla hoitaja meni kotiinsa, avasi kirjan ja huomasi: jokaisella sivulla luki vain 'kopoti-kopoti…'. Yritti arvata, oliko se kirjoitettu nuoteiksi vai vain todella tylsää laidasta laitaan."),
        Message("Pena", "Mies käveli puistossa ja näki kyltin 'Tänään ei naureta yhtään'. Hän hymyili, avasi taskunsa ja sanoi: 'Tämä vitsi tulee heti rikkomaan säännön!'"),
        Message("Pena", "Perhe ajoi hautausmaan ohi ja isä tokaisi: 'Tiedättekö miksi minua ei voi haudata tuonne?' Lapset kysyivät: 'Miksi?' Isä: 'Koska täällä ollaan jo tarpeeksi hiljaisia ilman minua.'"),
        Message("Pena", "Kaksi miestä jutteli: 'Minulla on niin hyvä muisti, että muistaisin jopa sen vitun punchlinen… jos vain muistaisin punchlinen sille vitulle.' Toinen vastasi: 'Ja minä unohdan vitsejä ennen kuin ehdin aloittaa.'"),
        Message("Pena", "Miksi hoitaja ei ottanut palkankorotusta vastaan? Hän sanoi: 'Jos minulla olisi lisää rahaa, minun pitäisi ostaa enemmän huumoria… ja sellaista ei myydä ilman pilkettä!'"),
        Message("Pena", "Mies meni kirjastoon ja kysyi: 'Onko teillä kirjaa nimeltä 'Miten kertoa vitsejä'?' Kirjastonhoitaja vastasi: 'On, mutta se on varattu kaikille, jotka eivät koskaan kerro vitsejä.'"),
        Message("Pena", "Eräs nainen kysyi mieheltään: 'Oletko koskaan nähnyt aidosti hauskaa vitsiä kirjassa?' Mies: 'En, mutta olen nähnyt kirjan, missä vitseissä oli huumoria ja se oli vieläpä suomeksi!'.")
    )
}