package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to merge flux.
 *
 * @author Sebastien Deleuze
 */
public class Part05MergeTest {

	final static User MARIE = new User("mschrader", "Marie", "Schrader");
	final static User MIKE = new User("mehrmantraut", "Mike", "Ehrmantraut");

	ReactiveRepository<User> repository1 = new ReactiveUserRepository(500);
	ReactiveRepository<User> repository2 = new ReactiveUserRepository(MARIE, MIKE);


	Part05Merge part05Merge = new Part05Merge();
//========================================================================================

	@Test
	public void mergeWithInterleave() {
		Flux<User> flux = part05Merge.mergeFluxWithInterleave(repository1.findAll(), repository2.findAll());
		StepVerifier.create(flux)
				.expectNext(MARIE, MIKE, User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
				.verifyComplete();
	}


//========================================================================================

	@Test
	public void mergeWithNoInterleave() {
		Flux<User> flux = part05Merge.mergeFluxWithNoInterleave(repository1.findAll(), repository2.findAll());
		StepVerifier.create(flux)
				.expectNext(User.SKYLER, User.JESSE, User.WALTER, User.SAUL, MARIE, MIKE)
				.verifyComplete();
	}


//========================================================================================

	@Test
	public void multipleMonoToFlux() {
		Mono<User> skylerMono = repository1.findFirst();
		Mono<User> marieMono = repository2.findFirst();
		Flux<User> flux = part05Merge.createFluxFromMultipleMono(skylerMono, marieMono);
		StepVerifier.create(flux)
				.expectNext(User.SKYLER, MARIE)
				.verifyComplete();
	}

}
