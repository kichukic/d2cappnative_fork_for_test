import express from 'express';
import controller from '../controller/Order';
import extractJWT from '../middleware/extractJWT';

const router = express.Router();

router.post('/ordercreate', controller.ordercreate);
router.get('/orderget/:orderId', extractJWT, controller.orderget);
router.get('/ordergetall', extractJWT, controller.ordergetall);
router.patch('/orderupdate/:orderId', extractJWT, controller.orderupdate);
router.delete('/orderdelete/:orderId', extractJWT, controller.orderdelete);

export = router;
