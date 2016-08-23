import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;
import com.pokegoapi.auth.PtcCredentialProvider;
/**
 * Created by alexander.nakoryakov on 19.08.2016.
 */
public class PokemonLogin {
    OkHttpClient httpClient = new OkHttpClient();
    PtcCredentialProvider provider = new PtcCredentialProvider(httpClient, LoginDetails.LOGIN, LoginDetails.PASSWORD);

    GoogleUserCredentialProvider googleProvider = new GoogleUserCredentialProvider(httpClient);

    public PokemonLogin() throws LoginFailedException, RemoteServerException {
    }

    public PokemonGo pokemonGo() throws LoginFailedException, RemoteServerException {
        //googleProvider.login(LoginDetails.TOKEN);
        PokemonGo pokemonGo = new PokemonGo(provider, httpClient);
        return pokemonGo;
    }
}
