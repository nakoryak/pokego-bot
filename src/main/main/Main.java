import POGOProtos.Map.Pokemon.MapPokemonOuterClass;
import POGOProtos.Map.Pokemon.NearbyPokemonOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by alexander.nakoryakov on 19.08.2016.
 */
public class Main {
    public static void main(String[] args) throws LoginFailedException, RemoteServerException, NoSuchItemException, InterruptedException {
        PokemonLogin login = new PokemonLogin();
        PokemonGo go = login.pokemonGo();

        go.setLocation(56.8457373, 60.5972259, 1.0);

        //Utils.printPokemons(go);
        System.out.println(go.getPlayerProfile().getStats().getLevel());
        //Utils.printPokemons(go);

        Utils.mainLoop(go);

    }
}
