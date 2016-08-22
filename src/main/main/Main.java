import POGOProtos.Inventory.Item.ItemIdOuterClass;
import POGOProtos.Map.Pokemon.MapPokemonOuterClass;
import POGOProtos.Map.Pokemon.NearbyPokemonOuterClass;
import POGOProtos.Networking.Requests.Messages.LevelUpRewardsMessageOuterClass;
import POGOProtos.Networking.Requests.RequestTypeOuterClass;
import POGOProtos.Networking.Responses.LevelUpRewardsResponseOuterClass;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.Map;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.EncounterFailedException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.main.ServerRequest;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;

/**
 * Created by alexander.nakoryakov on 19.08.2016.
 */
public class Main {
    public static void main(String[] args) throws LoginFailedException, RemoteServerException, NoSuchItemException, InterruptedException, EncounterFailedException {
        PokemonLogin login = new PokemonLogin();
        PokemonGo go = login.pokemonGo();

        go.setLocation(56.8400395, 60.601775, 1.0);

        //go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId., 100);
        //Utils.printInventory(go);

        System.out.println(go.getPlayerProfile().getStats().getLevel());
        System.out.println(go.getPlayerProfile().getStats().getExperience());
        System.out.println("Captured pokemons: " + go.getPlayerProfile().getStats().getUniquePokedexEntries());
        System.out.println("Count pokemons in bag: " + go.getInventories().getPokebank().getPokemons().size());
        //Utils.removePokemons(go);
        //Utils.printPokemons(go);

        Utils.mainLoop(go);

        /*for (int i = 15; i < 16; i++){
            LevelUpRewardsMessageOuterClass.LevelUpRewardsMessage msg = LevelUpRewardsMessageOuterClass.LevelUpRewardsMessage.newBuilder().setLevel(i).build();
            ServerRequest serverRequest = new ServerRequest(RequestTypeOuterClass.RequestType.LEVEL_UP_REWARDS, msg);
            go.getRequestHandler().sendServerRequests(serverRequest);

            // and get the response like this :

            LevelUpRewardsResponseOuterClass.LevelUpRewardsResponse response = null;
            try {
                response = LevelUpRewardsResponseOuterClass.LevelUpRewardsResponse.parseFrom(serverRequest.getData());
                response.getItemsAwardedList().stream().forEach( x -> {
                    System.out.println(x.getItemId().name() + " " + x.getItemCount());
                });
            } catch (InvalidProtocolBufferException e) {
                // its possible that the parsing fail when servers are in high load for example.
                throw new RemoteServerException(e);
            }
            Thread.sleep(3000);
        }*/

    }
}
