import mongoose,{ Document, now, Schema } from  "mongoose";

export interface IAgent {
    agentname: String;
    workpincode: String;
}

export interface IAgentModel extends IAgent,Document{}

const AgentSchema: Schema = new Schema(
    {
        agentname: { type: String, required: true },
        agentmobile: { type: String, required: true },
        workpincode: { type: String, required: true },
        agentemail: { type: String, required: true },
        password: { type: String, required: true }
    },
    {
        versionKey: false
    }
);

export default mongoose.model<IAgentModel>('Agent', AgentSchema);
