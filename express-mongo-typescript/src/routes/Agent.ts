import express from "express";
import controller from '../controller/Agent';
import extractJWT from "../middleware/extractJWT";

const router =express.Router();

router.post('/agentcreate', controller.createAgent);
router.get('/agentget/:agentId', extractJWT, controller.getAgent);
router.get('/agentgetall', extractJWT, controller.getAgentAll);
router.patch('/agentupdate/:agentId', extractJWT, controller.updateAgent);
router.delete('/agentdelete/:agentId', extractJWT, controller.deleteAgent);

router.get('/validatetoken', extractJWT, controller.validateToken);
// router.get('/validatetoken', controller.validateToken);
router.post('/agentlogin', controller.agentLogin);

// router.post('/create', ValidateJoi(Schemas.author.create), controller.createAuthor);
// router.patch('/update/:authorId', ValidateJoi(Schemas.author.update), controller.updateAuthor);

export = router;