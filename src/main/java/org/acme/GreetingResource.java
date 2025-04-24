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

		DummyObject sendObject = request.toBuilder().build();
		sendObject.setMethod("Send");
		eventBus.send("blocking-dummy-consumer", sendObject);
		eventBus.send("non-blocking-dummy-consumer", sendObject);
		mutinityEventBus.send("mutinity-blocking-dummy-consumer", sendObject);
		mutinityEventBus.send("mutinity-non-blocking-dummy-consumer", sendObject);

		DummyObject requestObject = request.toBuilder().build();
		requestObject.setMethod("Request");
		eventBus.request("blocking-dummy-consumer", requestObject);
		eventBus.request("non-blocking-dummy-consumer", requestObject);
		mutinityEventBus.request("mutinity-blocking-dummy-consumer", requestObject);
		mutinityEventBus.request("mutinity-non-blocking-dummy-consumer", requestObject);

		DummyObject publishObject = request.toBuilder().build();
		publishObject.setMethod("Publish");
		eventBus.publish("blocking-dummy-consumer", publishObject);
		eventBus.publish("non-blocking-dummy-consumer", publishObject);
		mutinityEventBus.publish("mutinity-blocking-dummy-consumer", publishObject);
		mutinityEventBus.publish("mutinity-non-blocking-dummy-consumer", publishObject);

		return Response.ok().build();
	}

	@ConsumeEvent(value = "blocking-dummy-consumer", blocking = true)
	void consumeBlockingDummyEvent(DummyObject obj) {
		Log.infov("Blocking {0}", obj);
	}

	@ConsumeEvent(value = "non-blocking-dummy-consumer", blocking = false)
	void consumeNonBlockingDummyEvent(DummyObject obj) {
		Log.infov("Non-blocking {0}", obj);
	}

	@ConsumeEvent(value = "mutinity-blocking-dummy-consumer", blocking = true)
	void consumeMutinityBlockingDummyEvent(DummyObject obj) {
		Log.infov("Mutinity: Blocking {0}", obj);
	}

	@ConsumeEvent(value = "mutinity-non-blocking-dummy-consumer", blocking = false)
	void consumeMutinityNonBlockingDummyEvent(DummyObject obj) {
		Log.infov("Mutinity: Non-blocking {0}", obj);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "Hello from Quarkus REST";
	}

}
