package com.example.mobilecomputinghw1

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilecomputinghw1.ui.theme.MobilecomputingHW1Theme

data class Message(val author: String, val body: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilecomputingHW1Theme {
                Conversation(SampleData.conversationSample)
            }
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

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    MobilecomputingHW1Theme {
        Conversation(SampleData.conversationSample)
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    MobilecomputingHW1Theme {
        Surface {
            MessageCard(
                msg = Message("Lexi", "Hey, take a look at Jetpack Compose, it's great!")
            )
        }
    }
}

object SampleData {
    val conversationSample = listOf(
        Message("Pena", "moro, haluutko kuulla vitsin tai pari"),
        Message("Pena", "Eräs Lada-kuski ajoi moottoritiellä tyytyväisenä, kunnes auto päätti lopettaa yhteistyön. Ferrari-kuski pysähtyi auttamaan ja kysyi: ’Tarvitsetko apua?’ Lada-kuski vastasi: ’Ei hätää, meillä on vika-valot… me vaan odotellaan, että tämä muuttuu luksusongelmaksi.’"),
        Message("Pena", "Mies meni baariin kertomaan vitsejä, mutta baarimikko sanoi: ’Täällä kaikki on kuultu.’ Mies vastasi: ’Ok, aloitan sitten siitä, mistä viimeksi jäi kertomatta… mutta ensin lasi vettä!’"),
        Message("Pena", "Hoitaja oli innoissaan, kun asiakas kertoi kirjoittaneensa kirjan. Illalla hoitaja meni kotiinsa, avasi kirjan ja huomasi: jokaisella sivulla luki vain ’kopoti-kopoti…’. Yritti arvata, oliko se kirjoitettu nuoteiksi vai vain todella tylsää laidasta laitaan." ),
        Message("Pena", "Mies käveli puistossa ja näki kyltin ’Tänään ei naureta yhtään’. Hän hymyili, avasi taskunsa ja sanoi: ’Tämä vitsi tulee heti rikkomaan säännön!’"),
        Message("Pena", "Perhe ajoi hautausmaan ohi ja isä tokaisi: ’Tiedättekö miksi minua ei voi haudata tuonne?’ Lapset kysyivät: ’Miksi?’ Isä: ’Koska täällä ollaan jo tarpeeksi hiljaisia ilman minua.’"),
        Message("Pena", "Kaksi miestä jutteli: ’Minulla on niin hyvä muisti, että muistaisin jopa sen vitun punchlinen… jos vain muistaisin punchlinen sille vitulle.’ Toinen vastasi: ’Ja minä unohdan vitsejä ennen kuin ehdin aloittaa.’"),
        Message("Pena", "Miksi hoitaja ei ottanut palkankorotusta vastaan? Hän sanoi: ’Jos minulla olisi lisää rahaa, minun pitäisi ostaa enemmän huumoria… ja sellaista ei myydä ilman pilkettä!’"),
        Message("Pena", "Mies meni kirjastoon ja kysyi: ’Onko teillä kirjaa nimeltä ’Miten kertoa vitsejä’?’ Kirjastonhoitaja vastasi: ’On, mutta se on varattu kaikille, jotka eivät koskaan kerro vitsejä.’"),
        Message("Pena", "Eräs nainen kysyi mieheltään: ’Oletko koskaan nähnyt aidosti hauskaa vitsiä kirjassa?’ Mies: ’En, mutta olen nähnyt kirjan, missä vitseissä oli huumoria ja se oli vieläpä suomeksi!’.")
    )



}
