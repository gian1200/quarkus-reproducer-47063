package org.acme;

import org.acme.dto.DummyObject;

import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class GreetingResource {

	private final io.vertx.core.eventbus.EventBus eventBus;
	private final io.vertx.mutiny.core.eventbus.EventBus mutinityEventBus;

	@Inject
	public GreetingResource(io.vertx.core.eventbus.EventBus eventBus,
			io.vertx.mutiny.core.eventbus.EventBus mutinityeventBus) {
		this.eventBus = eventBus;
		this.mutinityEventBus = mutinityeventBus;
	}

	@POST
	@Path("/dummy")
	public Response process(DummyObject request) {
		Log.infov("Initial log with traceId: {0}", request);

		eventBus.send("blocking-dummy-consumer", request);
		eventBus.send("non-blocking-dummy-consumer", request);

		mutinityEventBus.send("mutinity-blocking-dummy-consumer", request);
		mutinityEventBus.send("mutinity-non-blocking-dummy-consumer", request);

		return Response.ok().build();
	}

	@ConsumeEvent(value = "blocking-dummy-consumer", blocking = true)
	void consumeBlockingDummyEvent(DummyObject request) {
		Log.infov("this log doesn't have traceId: {0}", request);
	}

	@ConsumeEvent(value = "non-blocking-dummy-consumer", blocking = false)
	void consumeNonBlockingDummyEvent(DummyObject request) {
		Log.infov("this log has traceId: {0}", request);
	}

	@ConsumeEvent(value = "mutinity-blocking-dummy-consumer", blocking = true)
	void consumeMutinityBlockingDummyEvent(DummyObject request) {
		Log.infov("Mutinity: this log doesn't have traceId: {0}", request);
	}

	@ConsumeEvent(value = "mutinity-non-blocking-dummy-consumer", blocking = false)
	void consumeMutinityNonBlockingDummyEvent(DummyObject request) {
		Log.infov("Mutinity: this log has traceId: {0}", request);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Hello from Quarkus REST";
	}

}
